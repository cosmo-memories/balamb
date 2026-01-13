package cosmo_memories.Balamb.model.enums;

/**
 * Enum for update post types.
 */
public enum UpdateType {
    UPDATE("Update"),
    TODO("To Do"),
    ISSUE("Known Issue"),
    NEWS("News");

    private final String label;

    UpdateType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
