package org.uludag.bmb.operations.database;

import java.util.List;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.uludag.bmb.controller.database.DatabaseController;
import org.uludag.bmb.factory.query.QueryFactory;

@SuppressWarnings("unchecked")
public class DatabaseOperations {
    protected DatabaseController databaseController;

    public DatabaseOperations() {
        this.databaseController = new DatabaseController();
    }

    public <T> List<T> executeLocalQuery(QueryFactory query, Object... params) {
        ResultSetHandler<List<T>> rsh = new BeanListHandler<T>((Class<? extends T>) query.getClassType());
        try {
            if (query.getQuery().contains("UPDATE") || query.getQuery().contains("INSERT")
                    || query.getQuery().contains("DELETE")) {
                if (params.length == 0) {
                    databaseController.getLocalQueryRunner().update(query.getQuery());
                } else {
                    databaseController.getLocalQueryRunner().update(query.getQuery(), params);
                }
                return null;
            } else {
                if (params.length == 0) {
                    var result = databaseController.getLocalQueryRunner().query(query.getQuery(), rsh);
                    return result;
                } else {
                    var result = databaseController.getLocalQueryRunner().query(query.getQuery(), rsh, params);
                    return result;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T> List<T> executeCloudQuery(QueryFactory query, Object... params) {
        ResultSetHandler<List<T>> rsh = new BeanListHandler<T>((Class<? extends T>) query.getClassType());
        try {
            if (query.getQuery().contains("UPDATE") || query.getQuery().contains("INSERT")
                    || query.getQuery().contains("DELETE")) {
                if (params.length == 0) {
                    databaseController.getAzureQueryRunner().update(query.getQuery());
                } else {
                    databaseController.getAzureQueryRunner().update(query.getQuery(), params);
                }
                return null;
            } else {
                if (params.length == 0) {
                    var result = databaseController.getAzureQueryRunner().query(query.getQuery(), rsh);
                    return result;
                } else {
                    var result = databaseController.getAzureQueryRunner().query(query.getQuery(), rsh, params);
                    return result;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
