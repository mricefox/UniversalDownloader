package com.mricefox.mfdownloader.lib.persistence;

import java.util.List;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/12/1
 */
public interface Persistence<T> {
    List<T> readAll();

    long insert(T entity);

    long update(T entity);

    long delete(T entity);
}
