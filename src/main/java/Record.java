import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Record {
    private long account;
    private String name;
    private double value;

    public Record(Record record) {
        this.account = record.account;
        this.name = record.name;
        this.value = record.value;
    }

    @Override
    public String toString() {
        return "Record{" +
                "account=" + account +
                ", name='" + name + '\'' +
                ", value=" + value +
                "}\n";
    }

}
