package benchmarks;

import database.APIDatabase;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Function;


/**
 * Class in charge of common parts of an execution, such as initializing the database.
 */
public class MainUtility {
    
    private APIDatabase db;

    public MainUtility(APIDatabase db) {
        this.db = db;
    }

    private final int maxBufferSize = 128;

    private void populateDatabase(String tableName, String fileName,
                                              Function<String, String> idGenerator){
        var table = new HashMap<String, String>();
        var tableID = new ArrayList<>();

        if(!fileName.equals("{}")) {
            try {
                var file = new File(fileName);
                FileInputStream fis = new FileInputStream(fileName);
                FileChannel fis_fc = fis.getChannel();
                var totalBytes = file.length();
                StringBuilder sb = new StringBuilder();
                while(totalBytes > maxBufferSize){
                    ByteBuffer buf_read = ByteBuffer.allocate(maxBufferSize - 1);
                    totalBytes -= (maxBufferSize - 1);
                    fis_fc.read(buf_read);
                    var s = new String(buf_read.array());

                    sb.append(s);
                }
                ByteBuffer buf_read = ByteBuffer.allocate((int) totalBytes);
                fis_fc.read(buf_read);
                var s = new String(buf_read.array());
                sb.append(s);
                fis.close();


                var lines = sb.toString().split("\n");
                for(var row: lines){
                    tableID.add(idGenerator.apply(row));
                    if(!idGenerator.apply(row).equals(
                            row.replace(";", ":")))
                        table.put(tableName+":"+idGenerator.apply(row), row);
                }

            } catch (IOException e) {
                e.printStackTrace();

            }
        }
        db.write(tableName, tableID.toString());
        for(var e : table.entrySet()){
            db.write(e.getKey(), e.getValue());
        }
    }

    public void initTransaction(HashMap<String, String> tableInfo, HashSet<String> arraySet, Function<String, Function<String, String>> idGenerator){
        db.begin();

        for(var t : tableInfo.entrySet()){
            populateDatabase(t.getKey(), t.getValue(), idGenerator.apply(t.getKey()));

        }

        db.commit();
    }

}
