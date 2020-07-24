package de.kit.research.model.systemmodel.trace;

import de.kit.research.model.exception.InvalidTraceException;
import org.codehaus.plexus.util.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This class is a container for a whole trace of executions (represented as instances of {@link Execution}).
 * <p>
 * Note that no assumptions about the {@link java.util.concurrent.TimeUnit} used for the
 * timestamps are made.
 *
 * @author Andre van Hoorn
 * @since 0.95a
 */
public class ExecutionTrace extends AbstractTrace {

    // private static final Log LOG = LogFactory.getLog(ExecutionTrace.class);
    private final AtomicReference<MessageTrace> messageTrace = new AtomicReference<>();
    private int minEoi = -1;
    private int maxEoi = -1;
    private long minTin = -1;
    private long maxTout = -1;
    private int maxEss = -1;
    private final List<Execution> set = new ArrayList<>();
    private final List<Execution> invalidExecutions = new ArrayList<>();

    /**
     * Creates a new instance of this class using the given parameters.
     *
     * @param traceId The ID of this trace.
     */
    public ExecutionTrace(final String traceId) {
        super(traceId);
    }

    /**
     * Creates a new instance of this class using the given parameters.
     *
     * @param traceId   The ID of this trace.
     * @param sessionId The ID of the current session.
     */
    public ExecutionTrace(final String traceId, final String sessionId) {
        super(traceId, sessionId);
    }

    /**
     * Adds an execution to the trace.
     *
     * @param execution The execution object which will be added to this trace.
     * @throws InvalidTraceException If the traceId of the passed Execution object is not the same as the traceId of this ExecutionTrace object.
     */
    public void add(final Execution execution) throws InvalidTraceException {
        synchronized (this) {
            if (!StringUtils.equalsIgnoreCase(this.getTraceId(), execution.getTraceId())) {
                throw new InvalidTraceException("TraceId of new record (" + execution.getTraceId() + ") differs from Id of this trace (" + this.getTraceId() + ")");
            }
            if ((this.minTin < 0) || (execution.getTin() < this.minTin)) {
                this.minTin = execution.getTin();
            }
            if ((this.maxTout < 0) || (execution.getTout() > this.maxTout)) {
                this.maxTout = execution.getTout();
            }
            if ((this.minEoi < 0) || (execution.getEoi() < this.minEoi)) {
                this.minEoi = execution.getEoi();
            }
            if ((this.maxEoi < 0) || (execution.getEoi() > this.maxEoi)) {
                this.maxEoi = execution.getEoi();
            }
            if (execution.getEss() > this.maxEss) {
                this.maxEss = execution.getEss();
            }
            this.set.add(execution);
            // Invalidate the current message trace representation
            this.messageTrace.set(null);
        }
    }

    /**
     * Returns the message trace representation for this trace.<br/>
     * <p>
     * The transformation to a message trace is only computed during the first execution of this method. After this, the stored reference is returned --- unless
     * executions are added to the trace afterwards.
     *
     * @param rootExecution The root execution object.
     * @return The resulting message trace.
     * @throws InvalidTraceException If the given execution is somehow inconsistent or invalid.
     */
    public MessageTrace toMessageTrace(final Execution rootExecution) throws InvalidTraceException {
        synchronized (this) {
            MessageTrace mt = this.messageTrace.get();
            if (mt != null) {
                return mt;
            }

            final List<AbstractMessage> mSeq = new ArrayList<>();
            final Stack<AbstractMessage> curStack = new Stack<>();
            final Iterator<Execution> eSeqIt = this.set.iterator();

            Execution prevE = rootExecution;
            int prevEoi = -1;
            boolean expectingEntryCall = true; // used to make that entry call found in first iteration
            while (eSeqIt.hasNext()) {
                final Execution curE = eSeqIt.next();
                if (expectingEntryCall && (curE.getEss() != 0)) {
                    final InvalidTraceException ex = new InvalidTraceException("First execution must have ess " + "0 (found " + curE.getEss()
                            + ")\n Causing execution: " + curE);
                    // don't log and throw
                    // LOG.error("Found invalid trace:" + ex.getMessage()); // don't need the stack trace here
                    throw ex;
                }
                expectingEntryCall = false; // now we're happy
                if (prevEoi != (curE.getEoi() - 1)) {
                    final InvalidTraceException ex = new InvalidTraceException("Eois must increment by 1 --" + "but found sequence <" + prevEoi
                            + "," + curE.getEoi() + ">" + "(Execution: " + curE + ")");
                    // don't log and throw
                    // LOG.error("Found invalid trace:" + ex.getMessage()); // don't need the stack trace here
                    throw ex;
                }
                prevEoi = curE.getEoi();

                // First, we might need to clean up the stack for the next execution callMessage
                if ((!prevE.equals(rootExecution)) && (prevE.getEss() >= curE.getEss())) {
                    Execution curReturnReceiver; // receiverComponentName of return message
                    while (curStack.size() > curE.getEss()) {
                        final AbstractMessage poppedCall = curStack.pop();
                        prevE = poppedCall.getReceivingExecution();
                        curReturnReceiver = poppedCall.getSendingExecution();
                        final AbstractMessage m = new SynchronousReplyMessage(prevE.getTout(), prevE, curReturnReceiver);
                        mSeq.add(m);
                        prevE = curReturnReceiver;
                    }
                }
                // Now, we handle the current execution callMessage
                if (prevE.equals(rootExecution)) { // initial execution callMessage
                    final AbstractMessage m = new SynchronousCallMessage(curE.getTin(), rootExecution, curE);
                    mSeq.add(m);
                    curStack.push(m);
                } else if ((prevE.getEss() + 1) == curE.getEss()) { // usual callMessage with senderComponentName and receiverComponentName
                    final AbstractMessage m = new SynchronousCallMessage(curE.getTin(), prevE, curE);
                    mSeq.add(m);
                    curStack.push(m);
                } else if (prevE.getEss() < curE.getEss()) { // detect ess incrementation by > 1
                    final InvalidTraceException ex = new InvalidTraceException("Ess are only allowed to increment by 1 --"
                            + "but found sequence <" + prevE.getEss() + "," + curE.getEss() + ">" + "(Execution: " + curE + ")");
                    // don't log and throw
                    // LOG.error("Found invalid trace:" + ex.getMessage()); // don't need the stack trace here
                    throw ex;
                }
                if (!eSeqIt.hasNext()) { // empty stack completely, since no more executions
                    Execution curReturnReceiver; // receiverComponentName of return message
                    while (!curStack.empty()) {
                        final AbstractMessage poppedCall = curStack.pop();
                        prevE = poppedCall.getReceivingExecution();
                        curReturnReceiver = poppedCall.getSendingExecution();
                        final AbstractMessage m = new SynchronousReplyMessage(prevE.getTout(), prevE, curReturnReceiver);
                        mSeq.add(m);
                        prevE = curReturnReceiver;
                    }
                }
                prevE = curE; // prepair next loop
            }
            mt = new MessageTrace(this.getTraceId(), this.getSessionId(), mSeq);
            this.messageTrace.set(mt);
            return mt;
        }
    }

    /**
     * Returns the message trace representation for this trace.<br/>
     * <p>
     * The transformation to a message trace is only computed during the first execution of this method. After this, the stored reference is returned --- unless
     * executions are added to the trace afterwards.
     *
     * @param rootExecution The root execution object.
     * @return The resulting message trace.
     * @throws InvalidTraceException If the given execution is somehow inconsistent or invalid.
     */
    public MessageTrace toMessageTrace2(final Execution rootExecution) throws InvalidTraceException {
        synchronized (this) {
            MessageTrace mt = this.messageTrace.get();
            if (mt != null) {
                return mt;
            }

            final List<AbstractMessage> mSeq = new ArrayList<>();
            final Stack<AbstractMessage> curStack = new Stack<>();
            final Iterator<Execution> eSeqIt = this.set.iterator();

            Execution prevE = rootExecution;

            HashMap<String, Execution> executions = new HashMap<>();
            HashMap<String, Execution> inExecutions = new HashMap<>();
            invalidExecutions.forEach(i -> inExecutions.put(i.getSpanId(), i));
            set.forEach(t -> executions.put(t.getSpanId(), t));
            executions.put(rootExecution.getTraceId(), rootExecution);

            while (eSeqIt.hasNext()) {
                final Execution curE = eSeqIt.next();
                if (StringUtils.equalsIgnoreCase(curE.getChildOf(), rootExecution.getTraceId())) {
                    final AbstractMessage m = new SynchronousCallMessage(curE.getTin(), rootExecution, curE);
                    mSeq.add(m);
                    curStack.push(m);
                } else {
                    Execution validPrevE = executions.get(curE.getChildOf());
                    if (validPrevE != null) {
                        prevE = validPrevE;
                        final AbstractMessage m = new SynchronousCallMessage(curE.getTin(), prevE, curE);
                        mSeq.add(m);
                        curStack.push(m);
                    } else {
                        Execution invalidPrevE;
                        prevE = inExecutions.get(curE.getChildOf());
                        if (prevE != null) {
                            boolean invalid = true;
                            while (invalid) {
                                validPrevE = executions.get(prevE.getChildOf());
                                if (validPrevE != null) {
                                    prevE = validPrevE;
                                    invalid = false;
                                } else {
                                    invalidPrevE = inExecutions.get(prevE.getChildOf());
                                    if (invalidPrevE == null) {
                                        try {
                                            throw new Exception("error");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    prevE = invalidPrevE;
                                }
                            }
                            final AbstractMessage m = new SynchronousCallMessage(curE.getTin(), prevE, curE);
                            mSeq.add(m);
                            curStack.push(m);
                        }

                    }
                }
                if (!eSeqIt.hasNext()) { // empty stack completely, since no more executions
                    Execution curReturnReceiver; // receiverComponentName of return message
                    while (!curStack.empty()) {
                        final AbstractMessage poppedCall = curStack.pop();
                        prevE = poppedCall.getReceivingExecution();
                        curReturnReceiver = poppedCall.getSendingExecution();
                        final AbstractMessage m = new SynchronousReplyMessage(prevE.getTout(), prevE, curReturnReceiver);
                        mSeq.add(m);
                        prevE = curReturnReceiver;
                    }
                }
                prevE = curE; // prepair next loop
            }
            mt = new MessageTrace(this.getTraceId(), this.getSessionId(), mSeq);
            this.messageTrace.set(mt);
            return mt;
        }
    }

    /**
     * Returns the length of this trace in terms of the number of contained
     * executions.
     *
     * @return the length of this trace.
     */
    public final int getLength() {
        synchronized (this) {
            return this.set.size();
        }
    }

    /**
     * Returns the maximum execution stack size (ess) value, i.e., the maximum
     * stack depth, within the trace.
     *
     * @return the maximum ess; -1 if the trace contains no executions.
     */
    public int getMaxEss() {
        synchronized (this) {
            return this.maxEss;
        }
    }

    /**
     * Returns the maximum execution order index (eoi) value within the trace.
     *
     * @return the maximum eoi; -1 if the trace contains no executions.
     */
    public int getMaxEoi() {
        synchronized (this) {
            return this.maxEoi;
        }
    }

    /**
     * Returns the minimum execution order index (eoi) value within the trace.
     *
     * @return the minimum eoi; -1 if the trace contains no executions.
     */
    public int getMinEoi() {
        synchronized (this) {
            return this.minEoi;
        }
    }

    /**
     * Returns the duration of this (possibly incomplete) trace.
     * <p>
     * This value is the difference between the maximum tout and the minimum
     * tin value. Note that no specific assumptions about the {@link java.util.concurrent.TimeUnit} are made.
     *
     * @return the duration of this trace.
     */
    public long getDuration() {
        synchronized (this) {
            return this.getMaxTout() - this.minTin;
        }
    }

    /**
     * Returns the maximum timestamp value of an execution return in this trace.
     * <p>
     * Notice that you should need use this value to reason about the
     * control flow --- particularly in distributed scenarios.
     *
     * @return the maxmum timestamp value; -1 if the trace contains no executions.
     */
    public long getMaxTout() {
        synchronized (this) {
            return this.maxTout;
        }
    }

    /**
     * Returns the minimum timestamp of an execution start in this trace.
     * <p>
     * Notice that you should need use this value to reason about the
     * control flow --- particularly in distributed scenarios.
     *
     * @return the minimum timestamp value; -1 if the trace contains no executions.
     */
    public long getMinTin() {
        synchronized (this) {
            return this.minTin;
        }
    }

    @Override
    public long getStartTimestamp() {
        return this.getMinTin();
    }

    @Override
    public long getEndTimestamp() {
        return this.getMaxTout();
    }

    public List<Execution> getInvalidExecutions() {
        return invalidExecutions;
    }

    // Explicit delegation to super method to make FindBugs happy
    @Override
    public int hashCode() { // NOPMD (forward hashcode)
        return super.hashCode();
    }

    /**
     * Returns whether this Execution Trace and the passed Object are equal. Two execution traces are equal if the set of contained executions is equal.
     *
     * @param obj The object to be compared for equality with this.
     * @return true if and only if the two objects are equal.
     */
    @Override
    public boolean equals(final Object obj) {
        synchronized (this) {
            if (!(obj instanceof ExecutionTrace)) {
                return false;
            }
            if (this == obj) {
                return true;
            }
            final ExecutionTrace other = (ExecutionTrace) obj;
            if (!StringUtils.equalsIgnoreCase(this.getTraceId(), other.getTraceId())) {
                return false;
            }
            // Note that we are using a TreeSet which is not using the Object's equals
            // method but the Set's Comparator, which we defined in this case.
            return this.set.equals(other.set);
        }
    }

}
