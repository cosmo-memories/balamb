package cosmo_memories.Balamb.model.enums;

import org.springframework.data.domain.Sort.Direction;

/**
 * Enum for Sort methods.
 */
public enum SortOrder {
    ADDED("Date Added", "added", Direction.DESC),
    PUBYEAR("Date Published", "pubYear", Direction.DESC),
    PUBLISHER("Publisher", "publisher", Direction.ASC),
    TITLE("Title", "title", Direction.ASC);

    private final String label;
    private final String sort;
    private final Direction direction;

    SortOrder(String label, String sort, Direction direction) {
        this.label = label;
        this.sort = sort;
        this.direction = direction;
    }

    public String getLabel() {
        return label;
    }
    public String getSort() { return sort; }
    public Direction getDirection() { return direction; }
}
