package cosmo_memories.Balamb.model.enums;

public enum UpdateType {
    UPDATE("Update"),
    TODO("TODO"),
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
