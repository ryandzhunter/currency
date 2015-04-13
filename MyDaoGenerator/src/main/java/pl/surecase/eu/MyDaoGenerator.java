package pl.surecase.eu;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class MyDaoGenerator {

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(1, "greendao");

        Entity entity = schema.addEntity("GlobalCourses");
        entity.addIdProperty();
        entity.addStringProperty("usd");
        entity.addStringProperty("eur");
        entity.addDateProperty("date");
        entity.implementsSerializable();

        new DaoGenerator().generateAll(schema, args[0]);
    }
}
