package cyclops;

import com.aol.cyclops2.hkt.Higher;
import com.aol.cyclops2.types.Unit;
import cyclops.collections.ListX;
import cyclops.control.Maybe;
import cyclops.function.Fn1;
import cyclops.function.Monoid;
import cyclops.monads.AnyM;
import cyclops.monads.Witness;
import cyclops.monads.WitnessType;
import cyclops.stream.ReactiveSeq;
import cyclops.typeclasses.monad.Monad;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * Collection of useful functions
 * Also see {@link Semigroups}
 *          {@link Monoids}
 *          {@link cyclops.function.Predicates}
 *          {@link cyclops.function.Curry}
 *          {@link cyclops.function.CurryVariance}
 *          {@link cyclops.function.CurryConsumer}
 *          {@link cyclops.function.PartialApplicator}
 *          {@link cyclops.function.Memoize}
 *          {@link cyclops.function.FluentFunctions}
 *          {@link Fn1}F
 *          {@link Fn2}
 *          {@link Fn3}
 *          {@link Fn4}
 */
public class Functions {

    public static final  <T,R> Fn1<? super T,? extends R> constant(R r){
        return t->r;
    }

    public static final  <T> Fn1<? super T,? extends T> identity(){
        return t->t;
    }

    public static final  <T> Fn1<? super T,? extends Maybe<? extends T>> lifted(){
        return t-> Maybe.ofNullable(t);
    }

    /**
     * Kleisli arrow : A function that takes an input value t and embeds it inside a monadic context.
     * arrowM makes use of Witness Types to simulate higher-kinded types, and wraps the new monadic type
     * inside an AnyM. AnyM makes use of sub-type polymorphism (Object Orientd inheritance) to define monadic
     * functions (map / flatMap etc) on the returned Object (for parametric polymorphism use {@link Functions#arrow}
     *
     * @param w WitnessType Object: defines the returned monad type (e.g. see {@link Witness.stream} for HKT encoding for Streams)
     * @param <T> Value type to be embedded inside a monad
     * @param <W> The type of the WitnessType (Witness.stream, Witness.Future, Witness.list and so on)
     * @return A function that can embed a value inisde a Monad
     */
    public static final  <T,W extends WitnessType<W>> Fn1<? super T,? extends AnyM<W,T>> arrowM(W w){
        return t-> w.adapter().unit(t);
    }

    /**
     * Use an existing instance of a type that implements Unit to create a Kleisli arrow for that type
     *
     * <pre>
     *     {@code
     *      ListX<Integer> myList = ListX.of(1,2,3);
            Fn1<? super String, ? extends ListX<String>> arrow = Functions.arrowUnit(myList);

            ListX<String> list = arrow.apply("hello world");
     *
     *     }
     * </pre>
     *
     * @param w
     * @param <T>
     * @param <W>
     * @return
     */
    public static final  <T,W extends Unit<T>> Fn1<? super T,? extends W> arrowUnit(Unit<?> w){

        return t-> (W)w.unit(t);
    }

    public static final  <T,CRE> Fn1<? super T,? extends Higher<CRE,T>> arrow(Monad<CRE> monad){
        return t-> monad.unit(t);
    }

    public static final  <T> Fn1<? super Iterable<T>,? extends T> head(){
        return it -> ReactiveSeq.fromIterable(it).firstValue();
    }

    public static final  <T> Fn1<? super Iterable<T>,? extends T> tail(){
        return it -> ReactiveSeq.fromIterable(it)
                                .limitLast(1)
                                .firstValue();
    }
    public static final  <T> Fn1<? super Iterable<T>,? extends T> reduce(Monoid<T> monoid){
        return it -> ReactiveSeq.fromIterable(it)
                                .reduce(monoid.zero(),monoid);
    }

}
