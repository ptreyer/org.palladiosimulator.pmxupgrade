package de.kit.research.logic.modelcreation.util;

public class ModelCreationUtils {

    public static String createMethodKey(String signatureName, String componentName) {
        signatureName = signatureName.replace("<", "");
        signatureName = signatureName.replace(">", "");
        signatureName = componentName + signatureName;
        return signatureName;
    }

    public static String createSEFFKey(String methodName, String componentName) {
        return componentName + methodName;
    }


}
