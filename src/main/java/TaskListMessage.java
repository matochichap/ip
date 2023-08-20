import java.util.ArrayList;

public class TaskListMessage extends Message {
    private final String[] list;
    public TaskListMessage(ArrayList<Task> list) {
        this.list = formatList(list);
    }
    private String[] formatList(ArrayList<Task> list) {
        String[] ls = new String[list.size() + 1];
        int index = 1;
        for (Task item: list) {
            ls[index - 1] = String.format("%d. %s", index, item.toString());
            index++;
        }
        ls[index - 1] = horizontalLine;
        return ls;
    }
    @Override
    public void send() {
        System.out.println(createMessage(list));
    }
}
