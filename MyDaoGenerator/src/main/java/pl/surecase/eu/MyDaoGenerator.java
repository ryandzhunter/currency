package pl.surecase.eu;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class MyDaoGenerator {

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(1, "greendao");

        Entity entity = schema.addEntity("GlobalCourses");
        entity.addIdProperty();
        entity.addStringProperty("baseCurrency");
        entity.addStringProperty("quoteCurrency");
        entity.addDoubleProperty("value");
        entity.addDateProperty("date");
        entity.addStringProperty("course");

        new DaoGenerator().generateAll(schema, args[0]);
    }
}
