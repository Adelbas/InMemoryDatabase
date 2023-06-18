import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import java.util.*;

/**
 * Simple in memory database
 * 1. Предоставляет возможность добавлять новые записи;
 * 2. Предоставляет возможность удалять более не нужные записи;
 * 3. Предоставляет возможность изменять запись;
 * 4. Предоставляет возможность получать полный набор записи по любому из полей с одинаковой
 *    алгоритмической сложностью (не медленнее log(n));
 * 5. При получении записей, предоставляются их копии в силу неизменяемости;
 *
 * @author Mahmutov Adel
 */

@Slf4j
public class InMemoryDB {
    private final TreeMap<Long,Record> accounts;
    private final TreeMap<String, List<Record>> names;
    private final TreeMap<Double, List<Record>> values;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public InMemoryDB() {
        accounts = new TreeMap<>();
        names = new TreeMap<>();
        values = new TreeMap<>();
    }

    /**
     * Добавляет новую запись в базу данных.
     *
     * @param input новая запись
     * @return true если удалось добавить запись, false иначе
     */
    public boolean addRecord(Record input){
        Record record = Record.builder()
                .account(input.getAccount())
                .name(input.getName())
                .value(input.getValue())
                .build();
        if(accounts.containsKey(record.getAccount())){
            log.error("Failed to add record! Account {} is already exists!\n",record.getAccount());
            return false;
        }
        accounts.put(record.getAccount(),record);
        names.putIfAbsent(record.getName(),new ArrayList<>());
        names.get(record.getName()).add(record);
        values.putIfAbsent(record.getValue(),new ArrayList<>());
        values.get(record.getValue()).add(record);
        return true;
    }

    /**
     * Добавляет новую запись в формате json в базу данных.
     *
     * @param recordJson новая запись в формате json
     * @return true если удалось добавить запись, false иначе
     * @exception IllegalArgumentException в случае неправильного json
     */
    public boolean addRecord(String recordJson) {
        try {
            Record record = objectMapper.readValue(recordJson, Record.class);
            return addRecord(record);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid JSON!\n"+e);
        }
    }

    /**
     * Удаляет запись из базы данных.
     *
     * @param account id записи, которую нужно удалить
     * @return true если удалось удалить запись, false иначе
     */
    public boolean removeRecordByAccount(long account){
        if(!accounts.containsKey(account)){
            log.error("Failed to remove record! Account {} does not exists!\n",account);
            return false;
        }
        Record record = accounts.get(account);
        names.get(record.getName()).removeIf(rec->rec.getAccount()==record.getAccount());
        if(names.get(record.getName()).isEmpty())
            names.remove(record.getName());

        values.get(record.getValue()).removeIf(rec->rec.getAccount()== record.getAccount());
        if(values.get(record.getValue()).isEmpty())
            values.remove(record.getValue());

        accounts.remove(account);
        return true;
    }

    /**
     * Обновляет запись в базе данных.
     *
     * @param editedRecord изменённая запись
     * @return true если удалось изменить запись, false иначе
     */
    public boolean updateRecord(Record editedRecord){
        return removeRecordByAccount(editedRecord.getAccount()) && addRecord(editedRecord);
    }

    /**
     * Возвращает все записи из базы данных.
     *
     * @return список записей, либо пустой список если записей не существует
     */
    public Optional<List<Record>> getAllRecords() {
        return accounts.values().isEmpty()? Optional.empty():Optional.of(new ArrayList<>(accounts.values()));
    }

    /**
     * Возвращает запись из базы данных по полю "account".
     *
     * @param account поле "account" записи для поиска
     * @return найденная запись, либо пустое значение Optional если такой не существует
     */
    public Optional<Record> getRecordByAccount(long account){
        Optional<Record> record = Optional.ofNullable(accounts.get(account));
        return record.map(Record::new);
    }

    /**
     * Возвращает записи из базы данных по полю "name".
     *
     * @param name поле "name" записей для поиска
     * @return найденные записи, либо пустое значение Optional таких записей не существует
     */
    public Optional<List<Record>> getRecordsByName(String name) {
        Optional<List<Record>> records = Optional.ofNullable(names.get(name));
        return records.map(ArrayList::new);
    }

    /**
     * Возвращает записи из базы данных по полю "value".
     *
     * @param value поле "value" записей для поиска
     * @return найденные записи, либо пустое значение Optional таких записей не существует
     */
    public Optional<List<Record>> getRecordsByValue(double value) {
        Optional<List<Record>> records = Optional.ofNullable(values.get(value));
        return records.map(ArrayList::new);
    }
}
