package com.mricefox.mfdownloader.lib.persistence;

import java.util.List;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/12/1
 */
public interface Persistence<T> {
    /**
     * get all record, return null if fail
     *
     * @return
     */
    List<T> queryAll();

    /**
     * Insert download record, the id of entity will auto increse, return -1 if fail
     *
     * @param entity
     * @return
     */
    long insert(T entity);

    /**
     * Update entity by id, return -1 if not found
     *
     * @param entity
     * @return
     */
    long update(T entity);

    /**
     * delete download record by id, return -1 if not found
     *
     * @param entity
     * @return
     */
    long delete(T entity);

    /**
     * query record by id, return null if not found
     *
     * @param id
     * @return
     */
    T query(long id);
}
