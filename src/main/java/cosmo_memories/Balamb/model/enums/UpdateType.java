package cosmo_memories.Balamb.model.enums;

public enum UpdateType {
    UPDATE("Update"),
    TODO("To Do"),
    ISSUE("Known Issue"),
    NEWS("News"),
    BUG("Bug");

    private final String label;

    UpdateType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
