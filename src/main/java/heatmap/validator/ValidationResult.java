package heatmap.validator;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains the results of session validation.
 * Tracks validation errors and warnings.
 */
public class ValidationResult {
    private final List<String> errors;
    private final List<String> warnings;

    public ValidationResult() {
        this.errors = new ArrayList<>();
        this.warnings = new ArrayList<>();
    }

    /**
     * Adds an error message.
     *
     * @param error The error message
     */
    public void addError(String error) {
        errors.add(error);
    }

    /**
     * Adds a warning message.
     *
     * @param warning The warning message
     */
    public void addWarning(String warning) {
        warnings.add(warning);
    }

    /**
     * Checks if the validation passed (no errors).
     *
     * @return true if no errors, false otherwise
     */
    public boolean isValid() {
        return errors.isEmpty();
    }

    public List<String> getErrors() {
        return new ArrayList<>(errors);
    }

    public List<String> getWarnings() {
        return new ArrayList<>(warnings);
    }

    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ValidationResult{valid=").append(isValid());
        if (!errors.isEmpty()) {
            sb.append(", errors=").append(errors.size());
        }
        if (!warnings.isEmpty()) {
            sb.append(", warnings=").append(warnings.size());
        }
        sb.append("}");
        return sb.toString();
    }
}