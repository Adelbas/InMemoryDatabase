public class Main {
    public static void main(String[] args) {
        InMemoryDB database = new InMemoryDB();
        Record record1 = new Record(234678,"Иванов Иван Иванович",2035.34);
        Record record2 = new Record(234679,"Иванов Дмитрий Иванович",2121.35);
        Record record3 = new Record(234680,"Сидоров Анатолий Сергеевич",300.00);
        Record record4 = new Record(234681,"Иванов Иван Иванович",300.00);
        Record record5 = new Record(234682,"Кузнецова Екатерина Владимировна",0);
        String recordJson = """
                {
                  "account":303030, \s
                  "name":"Петров Пётр Петрович", \s
                  "value":44 \s
                }""";
        database.addRecord(record1);
        database.addRecord(record2);
        database.addRecord(record3);
        database.addRecord(record4);
        database.addRecord(record5);
        database.addRecord(recordJson);
        System.out.println("All records:\n"+database.getAllRecords());

        System.out.printf("\nUpdating record %d%n", record5.getAccount());
        record5.setValue(1500);
        database.updateRecord(record5);
        System.out.println(database.getRecordByAccount(234682).orElse(null));
        System.out.println("Records after update:\n"+database.getAllRecords());

        System.out.println("\nRecords by name 'Иванов Иван Иванович':\n"+database.getRecordsByName("Иванов Иван Иванович").orElse(null));
        System.out.println("\nRecords by value '300':\n"+database.getRecordsByValue(300).orElse(null));

        System.out.printf("\nDeletion record %d%n",record4.getAccount());
        database.removeRecordByAccount(record4.getAccount());

        System.out.println("\nRecords after deletion:\n"+database.getAllRecords());
        System.out.println("\nRecords by name 'Иванов Иван Иванович':\n"+database.getRecordsByName("Иванов Иван Иванович").orElse(null));
        System.out.println("\nRecords by value '300':\n"+database.getRecordsByValue(300).orElse(null));
    }
}
