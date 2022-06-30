package benchmarks;

import database.TRDatabase;
import database.TRUtility;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class BenchmarkModule {

    protected TRDatabase db;

    protected BenchmarkModule(){
        db = TRDatabase.getDatabase();
    }

    public abstract HashMap<String, Class<?>[]> getAllMethods(); //Name + List of args

    public Object executeTransaction(String transactionName, List<String> args) throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException, ClassNotFoundException, InstantiationException {

        var argsTypes = getAllMethods().get(transactionName);
        var method = this.getClass().getMethod(transactionName, argsTypes);

        if(args.size() != argsTypes.length) throw new InstantiationException("Wrong number of arguments for "+ transactionName +
                " (expected: "+ argsTypes.length+ ", received: "+args.size() +")");

        var objs = new ArrayList<>();

        for(int i = 0; i < args.size(); ++i){
            objs.add(constructObject(argsTypes[i], args.get(i)));
        }
        return method.invoke(this, objs.toArray());
    }

    protected Object constructObject(Class<?> type, String arg) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        switch (type.getName()){
            case "Integer":
            case "int":
                return Integer.parseInt(arg);
            case "Boolean":
            case "boolean":
                return Boolean.parseBoolean(arg);
            case "Float":
            case "float":
                return Float.parseFloat(arg);
            case "Long":
            case "long":
                return Long.parseLong(arg);
            case "Double":
            case "double":
                return Double.parseDouble(arg);
            case "[I":
                var ial =  TRUtility.generateArrayList(arg, (s)->Integer.parseInt(s));
                int [] ret = new int [ial.size()];
                for(int i = 0; i < ial.size(); ++i){
                    ret[i] = ial.get(i);
                }
                return ret;
            default:
                return type.getDeclaredConstructor(new Class[]{String.class}).newInstance(arg);
        }
    }
}
