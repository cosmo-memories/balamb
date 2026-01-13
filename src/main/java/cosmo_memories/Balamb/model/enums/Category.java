package cosmo_memories.Balamb.model.enums;

/**
 * Enum for book categories (fiction, nonfiction, etc.).
 */
public enum Category {
    FICTION("Fiction"),
    NONFICTION("Nonfiction"),
    GRAPHIC_NOVEL("Graphic Novel"),
    CHILDRENS("Children's");

    private final String label;

    Category(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
