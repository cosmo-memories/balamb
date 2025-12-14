package cosmo_memories.Balamb.model.enums;

public enum Genre {
    SCIFI("Science Fiction"),
    FANTASY("Fantasy"),
    HORROR("Horror"),
    ROMANCE("Romance"),
    GENFIC("General Fiction"),
    THRILLER("Thriller"),
    CRIME("Crime/Mystery"),
    GENNONFIC("General Nonfiction"),
    BIOGRAPHY("Biography"),
    ART("Art"),
    GAMING("Gaming");

    private final String label;

    Genre(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

}
