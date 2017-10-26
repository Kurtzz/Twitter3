package pl.edu.agh.ed.twitter3.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HibernateProperties {
    @Value("${hibernate.dialect}")
    private String dialect;

    @Value("${hibernate.show_sql}")
    private String showSql;

    @Value("${hibernate.hbm2ddl.auto}")
    private String hbm2dllAuto;

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    public String getShowSql() {
        return showSql;
    }

    public void setShowSql(String showSql) {
        this.showSql = showSql;
    }

    public String getHbm2dllAuto() {
        return hbm2dllAuto;
    }

    public void setHbm2dllAuto(String hbm2dllAuto) {
        this.hbm2dllAuto = hbm2dllAuto;
    }
}
