package com.terry.tingshu.helpers.jetdict;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
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

public class JetDict<K, V> implements MultiValueMap<K, V> {
    public JetDict() {
    }

    protected Map<K, List<V>> mSource = new LinkedHashMap<K, List<V>>();


    @Override
    public void add(K key, V value) {
        if (key != null) {
            if (!mSource.containsKey(key)) {
                mSource.put(key, new ArrayList<V>());
            }
            mSource.get(key).add(value);
        }
    }

    @Override
    public void add(K key, List<V> values) {
        for (V value : values) {
            add(key, value);
        }
    }

    @Override
    public void set(K key, V value) {
        mSource.remove(key);
        add(key, value);
    }

    @Override
    public void set(K key, List<V> values) {
        mSource.remove(key);
        add(key, values);
    }

    @Override
    public void set(Map<K, List<V>> map) {
        mSource.clear();
        mSource.putAll(map);
    }

    @Override
    public List<V> remove(K key) {
        return mSource.remove(key);
    }

    @Override
    public void clear() {
        mSource.clear();
    }

    @Override
    public Set<K> keySet() {
        return mSource.keySet();
    }

    @Override
    public List<K> keys() {
        List<K> keyList = new ArrayList<>() ;
        K[] ks = (K[]) mSource.keySet().toArray();
        return Arrays.asList(ks);
    }

    @Override
    public List<V> values() {
        List<V> allValues = new ArrayList<V>();
        Set<K> keySet = mSource.keySet();
        for (K key : keySet) {
            allValues.addAll(mSource.get(key));
        }
        return allValues;
    }

    @Override
    public V getValue(K key, int index) {
        List<V> values = mSource.get(key);
        if (values != null && index < values.size())
            return values.get(index);
        return null;
    }

    @Override
    public List<V> getValues(K key) {
        return mSource.get(key);
    }

    @Override
    public int size() {
        return mSource.size();
    }

    @Override
    public boolean isEmpty() {
        return mSource.isEmpty();
    }

    @Override
    public boolean containsKey(K key) {
        return mSource.containsKey(key);
    }
}
