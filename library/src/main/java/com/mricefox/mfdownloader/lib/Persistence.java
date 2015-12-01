package com.mricefox.mfdownloader.lib;

import java.util.List;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/12/1
 */
public interface Persistence<T> {
    List<T> readAll();

    boolean insert(T entity);

    long update(T entity);
}
