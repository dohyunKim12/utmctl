package config;


import java.util.ArrayList;
import java.util.List;

public class Global {
    public List<String> getFilter() {
        return filter;
    }

    public void setFilter(List<String> filter) {
        this.filter = filter;
    }

    private static class Holder {
        public static final Global instance = new Global();
    }

    public static Global getInstance() {
        return Holder.instance;
    }

    private ActionType caller;

    private List<String> filter = new ArrayList<>();

    public ActionType getCaller() {
        return caller;
    }

    public void setCaller(ActionType caller) {
        this.caller = caller;
    }

    public enum ActionType {
        GET(1),
        ADD(2),
        CANCEL(3),
        START(4),
        END(5),
        DESCRIBE(6),
        ADDBATCH(7),
        PROMOTE(8);

        private final int value;
        ActionType(int value) { this.value = value; }
        public int getValue() { return value; }

        public static ActionType fromValue(int value) {
            for (ActionType action : ActionType.values()) {
                if (action.value == value) {
                    return action;
                }
            }
            throw new IllegalArgumentException("No enum constant with value: " + value);
        }
    }

    public enum TaskStatus {
        ALL, PENDING, RUNNING, CANCELLED, COMPLETED, FAILED, PREEMPTED;
        public static TaskStatus fromString(String value) {
            for (TaskStatus status : TaskStatus.values()) {
                if (status.name().equalsIgnoreCase(value)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Invalid status: " + value + ". Allowed values: all, pending, running, cancelled, completed, failed, preempted.");
        }
    }

}
