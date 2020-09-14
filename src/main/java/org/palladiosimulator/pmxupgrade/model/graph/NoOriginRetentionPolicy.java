package org.palladiosimulator.pmxupgrade.model.graph;

public final class NoOriginRetentionPolicy extends AbstractOriginRetentionPolicy {

	private static final NoOriginRetentionPolicy INSTANCE = new NoOriginRetentionPolicy();

	private NoOriginRetentionPolicy() {
		super(OriginRetentionPolicyKind.NONE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IOriginRetentionPolicy uniteWith(final IOriginRetentionPolicy other) {
		return other;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> void handleOrigin(final AbstractGraphElement<T> element, final T origin) {
		// Do nothing
	}

	/**
	 * Factory method for the no-origin-retention policy.
	 * 
	 * @return See above
	 */
	public static NoOriginRetentionPolicy createInstance() {
		return INSTANCE;
	}
}
