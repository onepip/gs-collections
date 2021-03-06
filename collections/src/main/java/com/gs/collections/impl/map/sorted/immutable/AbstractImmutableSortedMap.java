/*
 * Copyright 2014 Goldman Sachs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gs.collections.impl.map.sorted.immutable;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.block.function.Function0;
import com.gs.collections.api.block.function.Function2;
import com.gs.collections.api.block.function.primitive.BooleanFunction;
import com.gs.collections.api.block.function.primitive.ByteFunction;
import com.gs.collections.api.block.function.primitive.CharFunction;
import com.gs.collections.api.block.function.primitive.DoubleFunction;
import com.gs.collections.api.block.function.primitive.FloatFunction;
import com.gs.collections.api.block.function.primitive.IntFunction;
import com.gs.collections.api.block.function.primitive.LongFunction;
import com.gs.collections.api.block.function.primitive.ShortFunction;
import com.gs.collections.api.block.predicate.Predicate;
import com.gs.collections.api.block.predicate.Predicate2;
import com.gs.collections.api.block.procedure.Procedure2;
import com.gs.collections.api.list.ImmutableList;
import com.gs.collections.api.list.primitive.ImmutableBooleanList;
import com.gs.collections.api.list.primitive.ImmutableByteList;
import com.gs.collections.api.list.primitive.ImmutableCharList;
import com.gs.collections.api.list.primitive.ImmutableDoubleList;
import com.gs.collections.api.list.primitive.ImmutableFloatList;
import com.gs.collections.api.list.primitive.ImmutableIntList;
import com.gs.collections.api.list.primitive.ImmutableLongList;
import com.gs.collections.api.list.primitive.ImmutableShortList;
import com.gs.collections.api.map.ImmutableMap;
import com.gs.collections.api.map.MutableMap;
import com.gs.collections.api.map.sorted.ImmutableSortedMap;
import com.gs.collections.api.map.sorted.MutableSortedMap;
import com.gs.collections.api.multimap.list.ImmutableListMultimap;
import com.gs.collections.api.multimap.sortedset.ImmutableSortedSetMultimap;
import com.gs.collections.api.partition.PartitionIterable;
import com.gs.collections.api.partition.list.PartitionImmutableList;
import com.gs.collections.api.partition.list.PartitionMutableList;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.impl.block.factory.Functions;
import com.gs.collections.impl.block.factory.Predicates;
import com.gs.collections.impl.block.procedure.MutatingAggregationProcedure;
import com.gs.collections.impl.block.procedure.NonMutatingAggregationProcedure;
import com.gs.collections.impl.block.procedure.PartitionPredicate2Procedure;
import com.gs.collections.impl.block.procedure.PartitionProcedure;
import com.gs.collections.impl.block.procedure.SelectInstancesOfProcedure;
import com.gs.collections.impl.block.procedure.primitive.CollectBooleanProcedure;
import com.gs.collections.impl.block.procedure.primitive.CollectByteProcedure;
import com.gs.collections.impl.block.procedure.primitive.CollectCharProcedure;
import com.gs.collections.impl.block.procedure.primitive.CollectDoubleProcedure;
import com.gs.collections.impl.block.procedure.primitive.CollectFloatProcedure;
import com.gs.collections.impl.block.procedure.primitive.CollectIntProcedure;
import com.gs.collections.impl.block.procedure.primitive.CollectLongProcedure;
import com.gs.collections.impl.block.procedure.primitive.CollectShortProcedure;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.list.mutable.primitive.BooleanArrayList;
import com.gs.collections.impl.list.mutable.primitive.ByteArrayList;
import com.gs.collections.impl.list.mutable.primitive.CharArrayList;
import com.gs.collections.impl.list.mutable.primitive.DoubleArrayList;
import com.gs.collections.impl.list.mutable.primitive.FloatArrayList;
import com.gs.collections.impl.list.mutable.primitive.IntArrayList;
import com.gs.collections.impl.list.mutable.primitive.LongArrayList;
import com.gs.collections.impl.list.mutable.primitive.ShortArrayList;
import com.gs.collections.impl.map.AbstractMapIterable;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.gs.collections.impl.map.sorted.mutable.TreeSortedMap;
import com.gs.collections.impl.multimap.list.FastListMultimap;
import com.gs.collections.impl.partition.list.PartitionFastList;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.tuple.ImmutableEntry;
import com.gs.collections.impl.utility.MapIterate;
import net.jcip.annotations.Immutable;

@Immutable
public abstract class AbstractImmutableSortedMap<K, V>
        extends AbstractMapIterable<K, V>
        implements ImmutableSortedMap<K, V>, SortedMap<K, V>
{
    public SortedMap<K, V> castToSortedMap()
    {
        return this;
    }

    public MutableSortedMap<K, V> toSortedMap()
    {
        return TreeSortedMap.newMap(this);
    }

    public ImmutableSortedMap<K, V> toImmutable()
    {
        return this;
    }

    public Iterator<V> iterator()
    {
        return this.valuesView().iterator();
    }

    public void putAll(Map<? extends K, ? extends V> map)
    {
        throw new UnsupportedOperationException("Cannot call putAll() on " + this.getClass().getSimpleName());
    }

    public void clear()
    {
        throw new UnsupportedOperationException("Cannot call clear() on " + this.getClass().getSimpleName());
    }

    public Set<Entry<K, V>> entrySet()
    {
        final MutableSet<Entry<K, V>> set = UnifiedSet.newSet(this.size());
        this.forEachKeyValue(new Procedure2<K, V>()
        {
            public void value(K key, V value)
            {
                set.add(ImmutableEntry.of(key, value));
            }
        });
        return set.toImmutable().castToSet();
    }

    public ImmutableSortedMap<K, V> newWithKeyValue(K key, V value)
    {
        TreeSortedMap<K, V> sortedMap = TreeSortedMap.newMap(this);
        sortedMap.put(key, value);
        return sortedMap.toImmutable();
    }

    public ImmutableSortedMap<K, V> newWithAllKeyValues(Iterable<? extends Pair<? extends K, ? extends V>> keyValues)
    {
        TreeSortedMap<K, V> sortedMap = TreeSortedMap.newMap(this);
        for (Pair<? extends K, ? extends V> keyValuePair : keyValues)
        {
            sortedMap.put(keyValuePair.getOne(), keyValuePair.getTwo());
        }
        return sortedMap.toImmutable();
    }

    public ImmutableSortedMap<K, V> newWithAllKeyValueArguments(Pair<? extends K, ? extends V>... keyValuePairs)
    {
        TreeSortedMap<K, V> sortedMap = TreeSortedMap.newMap(this);
        for (Pair<? extends K, ? extends V> keyValuePair : keyValuePairs)
        {
            sortedMap.put(keyValuePair.getOne(), keyValuePair.getTwo());
        }
        return sortedMap.toImmutable();
    }

    public ImmutableSortedMap<K, V> newWithoutKey(K key)
    {
        TreeSortedMap<K, V> sortedMap = TreeSortedMap.newMap(this);
        sortedMap.removeKey(key);
        return sortedMap.toImmutable();
    }

    public ImmutableSortedMap<K, V> newWithoutAllKeys(Iterable<? extends K> keys)
    {
        TreeSortedMap<K, V> sortedMap = TreeSortedMap.newMap(this);
        for (K key : keys)
        {
            sortedMap.removeKey(key);
        }
        return sortedMap.toImmutable();
    }

    public V put(K key, V value)
    {
        throw new UnsupportedOperationException("Cannot call put() on " + this.getClass().getSimpleName());
    }

    public V remove(Object key)
    {
        throw new UnsupportedOperationException("Cannot call remove() on " + this.getClass().getSimpleName());
    }

    public ImmutableSortedSetMultimap<V, K> flip()
    {
        return MapIterate.flip(this).toImmutable();
    }

    public ImmutableList<V> select(Predicate<? super V> predicate)
    {
        return this.select(predicate, FastList.<V>newList(this.size())).toImmutable();
    }

    @Override
    public <P> ImmutableList<V> selectWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.select(Predicates.bind(predicate, parameter));
    }

    public ImmutableSortedMap<K, V> select(Predicate2<? super K, ? super V> predicate)
    {
        return MapIterate.selectMapOnEntry(this, predicate, TreeSortedMap.<K, V>newMap(this.comparator())).toImmutable();
    }

    public ImmutableList<V> reject(Predicate<? super V> predicate)
    {
        return this.reject(predicate, FastList.<V>newList(this.size())).toImmutable();
    }

    @Override
    public <P> ImmutableList<V> rejectWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.reject(Predicates.bind(predicate, parameter));
    }

    public ImmutableSortedMap<K, V> reject(Predicate2<? super K, ? super V> predicate)
    {
        return MapIterate.rejectMapOnEntry(this, predicate, TreeSortedMap.<K, V>newMap(this.comparator())).toImmutable();
    }

    public PartitionImmutableList<V> partition(Predicate<? super V> predicate)
    {
        PartitionMutableList<V> partitionFastList = new PartitionFastList<V>();
        this.forEach(new PartitionProcedure<V>(predicate, partitionFastList));
        return partitionFastList.toImmutable();
    }

    public <P> PartitionIterable<V> partitionWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        PartitionMutableList<V> partitionFastList = new PartitionFastList<V>();
        this.forEach(new PartitionPredicate2Procedure<V, P>(predicate, parameter, partitionFastList));
        return partitionFastList.toImmutable();
    }

    public <S> ImmutableList<S> selectInstancesOf(Class<S> clazz)
    {
        FastList<S> result = FastList.newList(this.size());
        this.forEach(new SelectInstancesOfProcedure<S>(clazz, result));
        return result.toImmutable();
    }

    @Override
    public <R> ImmutableList<R> collect(Function<? super V, ? extends R> function)
    {
        return this.collect(function, FastList.<R>newList(this.size())).toImmutable();
    }

    @Override
    public ImmutableBooleanList collectBoolean(BooleanFunction<? super V> booleanFunction)
    {
        BooleanArrayList result = new BooleanArrayList(this.size());
        this.forEach(new CollectBooleanProcedure<V>(booleanFunction, result));
        return result.toImmutable();
    }

    public ImmutableByteList collectByte(ByteFunction<? super V> byteFunction)
    {
        ByteArrayList result = new ByteArrayList(this.size());
        this.forEach(new CollectByteProcedure<V>(byteFunction, result));
        return result.toImmutable();
    }

    public ImmutableCharList collectChar(CharFunction<? super V> charFunction)
    {
        CharArrayList result = new CharArrayList(this.size());
        this.forEach(new CollectCharProcedure<V>(charFunction, result));
        return result.toImmutable();
    }

    public ImmutableDoubleList collectDouble(DoubleFunction<? super V> doubleFunction)
    {
        DoubleArrayList result = new DoubleArrayList(this.size());
        this.forEach(new CollectDoubleProcedure<V>(doubleFunction, result));
        return result.toImmutable();
    }

    public ImmutableFloatList collectFloat(FloatFunction<? super V> floatFunction)
    {
        FloatArrayList result = new FloatArrayList(this.size());
        this.forEach(new CollectFloatProcedure<V>(floatFunction, result));
        return result.toImmutable();
    }

    public ImmutableIntList collectInt(IntFunction<? super V> intFunction)
    {
        IntArrayList result = new IntArrayList(this.size());
        this.forEach(new CollectIntProcedure<V>(intFunction, result));
        return result.toImmutable();
    }

    public ImmutableLongList collectLong(LongFunction<? super V> longFunction)
    {
        LongArrayList result = new LongArrayList(this.size());
        this.forEach(new CollectLongProcedure<V>(longFunction, result));
        return result.toImmutable();
    }

    public ImmutableShortList collectShort(ShortFunction<? super V> shortFunction)
    {
        ShortArrayList result = new ShortArrayList(this.size());
        this.forEach(new CollectShortProcedure<V>(shortFunction, result));
        return result.toImmutable();
    }

    public <K2, V2> ImmutableMap<K2, V2> collect(Function2<? super K, ? super V, Pair<K2, V2>> function)
    {
        return MapIterate.collect(this, function, UnifiedMap.<K2, V2>newMap()).toImmutable();
    }

    @Override
    public <P, VV> ImmutableList<VV> collectWith(Function2<? super V, ? super P, ? extends VV> function, P parameter)
    {
        return this.collect(Functions.bind(function, parameter));
    }

    public <R> ImmutableList<R> collectIf(Predicate<? super V> predicate, Function<? super V, ? extends R> function)
    {
        return this.collectIf(predicate, function, FastList.<R>newList(this.size())).toImmutable();
    }

    public <R> ImmutableSortedMap<K, R> collectValues(Function2<? super K, ? super V, ? extends R> function)
    {
        return MapIterate.collectValues(this, function, TreeSortedMap.<K, R>newMap(this.comparator())).toImmutable();
    }

    public Pair<K, V> detect(Predicate2<? super K, ? super V> predicate)
    {
        return MapIterate.detect(this, predicate);
    }

    @Override
    public <P> V detectWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.detect(Predicates.bind(predicate, parameter));
    }

    @Override
    public <P> V detectWithIfNone(Predicate2<? super V, ? super P> predicate, P parameter, Function0<? extends V> function)
    {
        return this.detectIfNone(Predicates.bind(predicate, parameter), function);
    }

    public <R> ImmutableList<R> flatCollect(Function<? super V, ? extends Iterable<R>> function)
    {
        return this.flatCollect(function, FastList.<R>newList(this.size())).toImmutable();
    }

    public <S> ImmutableList<Pair<V, S>> zip(Iterable<S> that)
    {
        return this.zip(that, FastList.<Pair<V, S>>newList(this.size())).toImmutable();
    }

    public ImmutableList<Pair<V, Integer>> zipWithIndex()
    {
        return this.zipWithIndex(FastList.<Pair<V, Integer>>newList(this.size())).toImmutable();
    }

    public SortedMap<K, V> subMap(K fromKey, K toKey)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".subMap() not implemented yet");
    }

    public SortedMap<K, V> headMap(K toKey)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".headMap() not implemented yet");
    }

    public SortedMap<K, V> tailMap(K fromKey)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".tailMap() not implemented yet");
    }

    public <R> ImmutableListMultimap<R, V> groupBy(Function<? super V, ? extends R> function)
    {
        return this.groupBy(function, FastListMultimap.<R, V>newMultimap()).toImmutable();
    }

    public <R> ImmutableListMultimap<R, V> groupByEach(Function<? super V, ? extends Iterable<R>> function)
    {
        return this.groupByEach(function, FastListMultimap.<R, V>newMultimap()).toImmutable();
    }

    public <V1> ImmutableMap<V1, V> groupByUniqueKey(Function<? super V, ? extends V1> function)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".groupByUniqueKey() not implemented yet");
    }

    public <K2, V2> ImmutableMap<K2, V2> aggregateInPlaceBy(
            Function<? super V, ? extends K2> groupBy,
            Function0<? extends V2> zeroValueFactory,
            Procedure2<? super V2, ? super V> mutatingAggregator)
    {
        MutableMap<K2, V2> map = UnifiedMap.newMap();
        this.forEach(new MutatingAggregationProcedure<V, K2, V2>(map, groupBy, zeroValueFactory, mutatingAggregator));
        return map.toImmutable();
    }

    public <K2, V2> ImmutableMap<K2, V2> aggregateBy(
            Function<? super V, ? extends K2> groupBy,
            Function0<? extends V2> zeroValueFactory,
            Function2<? super V2, ? super V, ? extends V2> nonMutatingAggregator)
    {
        MutableMap<K2, V2> map = UnifiedMap.newMap();
        this.forEach(new NonMutatingAggregationProcedure<V, K2, V2>(map, groupBy, zeroValueFactory, nonMutatingAggregator));
        return map.toImmutable();
    }
}
