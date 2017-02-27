package com.terry.tingshu.helpers.jetdict;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by terry on 2017/1/22.
 * <p>
 * Package Name: com.terry.tingshu.helpers.jetdict
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public interface MultiValueMap<K, V> {
    /**
     * 添加Key-Value
     *
     * @param key
     * @param value
     */
    void add(K key, V value);

    /**
     * 添加Key-List<Value>
     *
     * @param key
     * @param values
     */
    void add(K key, List<V> values);

    /**
     * 设置一个Key-Value，如果这个Key存在就被替换，不存在则被添加。
     *
     * @param key   key.
     * @param value values.
     */
    void set(K key, V value);

    /**
     * 设置Key-List<Value>，如果这个Key存在就被替换，不存在则被添加。
     *
     * @param key    key.
     * @param values values.
     *               <a href="http://www.jobbole.com/members/heydee@qq.com">@see</a> #set(Object, Object)
     */
    void set(K key, List<V> values);

    /**
     * 替换所有的Key-List<Value>。
     *
     * @param map values.
     */
    void set(Map<K, List<V>> map);

    /**
     * 移除某一个Key，对应的所有值也将被移除。
     *
     * @param key key.
     * @return value.
     */
    List<V> remove(K key);

    /**
     * 移除所有的值。
     * Remove all key-value.
     */
    void clear();

    /**
     * 拿到Key的集合。
     *
     * @return Set.
     */
    Set<K> keySet();

    /**
     * 拿到Key的集合。
     *
     * @return Set.
     */
    List<K> keys();

    /**
     * 拿到所有的值的集合。
     *
     * @return List.
     */
    List<V> values();

    /**
     * 拿到某一个Key下的某一个值。
     *
     * @param key   key.
     * @param index index value.
     * @return The value.
     */
    V getValue(K key, int index);

    /**
     * 拿到某一个Key的所有值。
     *
     * @param key key.
     * @return values.
     */
    List<V> getValues(K key);

    /**
     * 拿到MultiValueMap的大小.
     *
     * @return size.
     */
    int size();

    /**
     * 判断MultiValueMap是否为null.
     *
     * @return True: empty, false: not empty.
     */
    boolean isEmpty();

    /**
     * 判断MultiValueMap是否包含某个Key.
     *
     * @param key key.
     * @return True: contain, false: none.
     */
    boolean containsKey(K key);
}
