import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class InMemoryDBTest {
    private InMemoryDB database;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup(){
        database = new InMemoryDB();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testAddRecord() {
        Record record = new Record(234678, "Иванов Иван Иванович", 2035.34);

        assertTrue(database.addRecord(record));
        Optional<Record> recordByAccount = database.getRecordByAccount(record.getAccount());

        assertTrue(recordByAccount.isPresent());
        assertEquals(record, recordByAccount.get());
    }

    @Test
    public void testAddExistentRecord() {
        Record record = new Record(234678, "Иванов Иван Иванович", 2035.34);

        assertTrue(database.addRecord(record));
        log.info("Trying to add existent account: {}",record);
        assertFalse(database.addRecord(record));
    }

    @Test
    public void testAddRecordJSON() throws JsonProcessingException {
        Record record = new Record(234678, "Иванов Иван Иванович", 2035.34);

        String recordJson = objectMapper.writeValueAsString(record);
        assertTrue(database.addRecord(recordJson));
        Optional<Record> recordByAccount = database.getRecordByAccount(record.getAccount());

        assertTrue(recordByAccount.isPresent());
        assertEquals(record,recordByAccount.get());
    }

    @Test
    public void testAddWrongJson() {
        String recordJson = "some wrong json";
        assertThrows(IllegalArgumentException.class, ()->database.addRecord(recordJson));
    }

    @Test
    public void testRemoveRecordByAccount() {
        Record record = new Record(234678, "Иванов Иван Иванович", 2035.34);

        assertTrue(database.addRecord(record));
        assertTrue(database.removeRecordByAccount(record.getAccount()));
        Optional<Record> recordByAccount = database.getRecordByAccount(record.getAccount());

        assertTrue(recordByAccount.isEmpty());
        assertThrows(NoSuchElementException.class, recordByAccount::get);
    }

    @Test
    public void testRemoveNonExistentRecord() {
        long accountIndex = 1L;
        log.info("Trying to delete non-existent account: {}\n",accountIndex);
        assertTrue(database.getRecordByAccount(accountIndex).isEmpty());
        assertFalse(database.removeRecordByAccount(accountIndex));
    }

    @Test
    public void testUpdateRecord() {
        Record record = new Record(234678, "Иванов Иван Иванович", 2035.34);

        assertTrue(database.addRecord(record));
        record.setName("Edited name");
        record.setValue(0);
        assertTrue(database.updateRecord(record));

        Optional<Record> recordByAccount = database.getRecordByAccount(record.getAccount());
        assertTrue(recordByAccount.isPresent());
        assertEquals(record,recordByAccount.get());
    }

    @Test
    public void testRecordImmutability() {
        Record record = new Record(234678, "Иванов Иван Иванович", 2035.34);

        assertTrue(database.addRecord(record));
        record.setName("Edited name");

        Optional<Record> recordByAccount = database.getRecordByAccount(record.getAccount());
        assertTrue(recordByAccount.isPresent());
        assertNotEquals(record,recordByAccount.get());
    }

    @Test
    public void testGetAllRecords() {
        Record record1 = new Record(1L, "TestName1", 10);
        Record record2 = new Record(2L, "TestName2", 20);
        Record record3 = new Record(3L, "TestName3", 30);

        List<Record> inputRecords = new ArrayList<>(List.of(record1,record2,record3));
        assertTrue(database.addRecord(record1));
        assertTrue(database.addRecord(record2));
        assertTrue(database.addRecord(record3));

        Optional<List<Record>> records = database.getAllRecords();

        assertTrue(records.isPresent());
        assertNotNull(records);
        assertEquals(inputRecords, records.get());
    }

    @Test
    public void testListImmutability() {
        Record record1 = new Record(1L, "TestName1", 10);
        Record record2 = new Record(2L, "TestName2", 20);
        Record record3 = new Record(3L, "TestName3", 30);

        assertTrue(database.addRecord(record1));
        assertTrue(database.addRecord(record2));
        assertTrue(database.addRecord(record3));

        Optional<List<Record>> recordsBefore = database.getAllRecords();
        assertTrue(recordsBefore.isPresent());
        assertNotNull(recordsBefore);
        recordsBefore.get().clear();

        Optional<List<Record>> recordsAfter = database.getAllRecords();
        assertTrue(recordsAfter.isPresent());
        assertNotEquals(recordsBefore, recordsAfter);
    }

    @Test
    public void testGetRecordsByName() {
        Record record1 = new Record(1L, "TestName1", 10);
        Record record2 = new Record(2L, "TestName1", 20);
        Record record3 = new Record(3L, "TestName2", 10);

        assertTrue(database.addRecord(record1));
        assertTrue(database.addRecord(record2));
        assertTrue(database.addRecord(record3));

        Optional<List<Record>> recordsByName1 = database.getRecordsByName("TestName1");
        assertTrue(recordsByName1.isPresent());
        assertEquals(2, recordsByName1.get().size());
        assertTrue(recordsByName1.get().contains(record1));
        assertTrue(recordsByName1.get().contains(record2));

        Optional<List<Record>> recordsByName2 = database.getRecordsByName("TestName2");
        assertTrue(recordsByName2.isPresent());
        assertEquals(1, recordsByName2.get().size());
        assertTrue(recordsByName2.get().contains(record3));

        Optional<List<Record>> fakeName = database.getRecordsByName("Fake");
        assertTrue(fakeName.isEmpty());
    }

    @Test
    public void testGetRecordsByValue() {
        Record record1 = new Record(1L, "TestName1", 10);
        Record record2 = new Record(2L, "TestName1", 20);
        Record record3 = new Record(3L, "TestName2", 10);

        assertTrue(database.addRecord(record1));
        assertTrue(database.addRecord(record2));
        assertTrue(database.addRecord(record3));

        Optional<List<Record>> recordsByValue1 = database.getRecordsByValue(10);
        assertTrue(recordsByValue1.isPresent());
        assertEquals(2, recordsByValue1.get().size());
        assertTrue(recordsByValue1.get().contains(record1));
        assertTrue(recordsByValue1.get().contains(record3));

        Optional<List<Record>> recordsByValue2 = database.getRecordsByValue(20);
        assertTrue(recordsByValue2.isPresent());
        assertEquals(1, recordsByValue2.get().size());
        assertTrue(recordsByValue2.get().contains(record2));

        Optional<List<Record>> fakeValue = database.getRecordsByValue(234.23);
        assertTrue(fakeValue.isEmpty());
    }
}
