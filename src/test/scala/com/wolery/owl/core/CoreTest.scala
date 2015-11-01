//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : Unit tests for the core package.
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*
//*
//****************************************************************************

package com.wolery.owl.core;

import org.scalacheck.Arbitrary
import org.scalacheck.Gen._
import org.scalatest.FunSuite
import org.scalatest.Assertions._
import org.scalatest.prop.PropertyChecks

//****************************************************************************

class CoreTest extends FunSuite
{
  import CoreTest._

  implicit val α = Arbitrary(choose(-128  ,128))
  implicit val β = Arbitrary(choose(-128.0,128.0))

  test("ℤ is a ℤ-torsor")                 {isTorsor[ℤ,ℤ]()}
  test("ℤ(+) is commutative")             {isCommutative[ℤ]()}
  test("ℝ is an R-torsor")                {isTorsor[ℝ,ℝ]()}
  test("ℝ(+) is commutative")             {isCommutative[ℝ]()}
}

object CoreTest extends PropertyChecks
{
  /**
   * Check that the monoid operation for ''M'' is commutative.
   *
   * @see [[https://en.wikipedia.org/wiki/Commutative_property]]
   */
  def isCommutative[M]()(implicit α: Monoid[M],β: Arbitrary[M]): Unit =
  {
    forAll("a","b") {(a: M,b: M) ⇒
    {
      assert(a + b == b + a,              "[commutative]")
    }}
  }

  /**
   * Check that ''M'' satisfies the axioms of a monoid.
   *
   * @see [[https://en.wikipedia.org/wiki/Monoid]]
   */
  def isMonoid[M]()(implicit α: Monoid[M],β: Arbitrary[M]): Unit =
  {
    forAll("a") {(a: M) ⇒
    {
      assert(α.zero + a == a,             "[left identity]")
      assert(a + α.zero == a,             "[right identity]")
    }}

    forAll("a","b","c") {(a: M,b: M,c: M) ⇒
    {
      assert(a + (b + c) == (a + b) + c,  "[associative]")
    }}
  }

  /**
   * Check that ''G'' satisfies the axioms of a group.
   *
   * @see [[https://en.wikipedia.org/wiki/Group_(mathematics)]]
   */
  def isGroup[G]()(implicit α: Group[G],β: Arbitrary[G]): Unit =
  {
    isMonoid[G]()

    forAll("a") {(a: G) ⇒
    {
      assert(a + -a == α.zero,            "[negation]")
    }}
  }

  /**
   * Check that ''S'' satisfies the axioms of a ''G''-set; namely that `apply`
   * effects a homomorphism from ''G'' into Aut(''S'').
   *
   * @see [[https://en.wikipedia.org/wiki/Group_action]]
   */
  def isAction[S,G]()(implicit α: Action[S,G],β: Arbitrary[S],γ: Arbitrary[G]) : Unit =
  {
    isGroup[G]()

    forAll("s") {(s: S) ⇒
    {
      assert(s + α.zero == s,             "[identity]")
    }}

    forAll("s","f","g") {(s: S,f: G,g: G) ⇒
    {
      assert(s + (f + g) == (s + f) + g,  "[homomorphism +]")
      assert(s - (f + g) == (s - f) - g,  "[homomorphism -]")
      assert(s + f == s - -f,             "[negation +]")
      assert(s - f == s + -f,             "[negation -]")
    }}
  }

  /**
   * Check that ''S'' satisfies the axioms of a ''G''-torsor, namely that the
   * action of ''G'' upon ''S'' is sharply transitive.
   *
   * @see https://en.wikipedia.org/wiki/Principal_homogeneous_space]]
   */
  def isTorsor[S,G]()(implicit α: Torsor[S,G],β: Arbitrary[S],γ: Arbitrary[G]) : Unit =
  {
    isAction[S,G]()

    forAll("s","t") {(s: S,t: S) ⇒
    {
      assert(s + (t - s) == t,            "[interval]")
      assert(s + (s ⊣ t) == t,            "[forward interval]")
      assert(s + (t ⊢ s) == t,            "[reverse interval]")
      assert(s ⊣ t == t ⊢ s,              "[mutually inverse]")
    }}
  }

  /**
   * Check that ''S'' satisfies the axioms of a transposing set; namely that
   * it is a ℤ-set.
   */
  def isTransposing[S]()(implicit α: Transposing[S],β: Arbitrary[S],γ: Arbitrary[ℤ]) : Unit =
  {
    isAction[S,ℤ]()
  }

  /**
   * Check that ''S'' satisfies the axioms of an intervallic set; namely, that
   * it is a ℤ-torsor.
   */
  def isIntervallic[S]()(implicit α: Intervallic[S],β: Arbitrary[S],γ: Arbitrary[ℤ]) : Unit =
  {
    isTorsor[S,ℤ]()

    forAll("s") {(s: S) ⇒
    {
      assert(s.♭ == s - 1,                "[flat]")
      assert(s.♮ == s    ,                "[natural]")
      assert(s.♯ == s + 1,                "[sharp]")
    }}
  }

  /**
   * Check that the mapping ''f'' satisfies the axioms of an equivariant map;
   * namely that it commutes with the action of ''G'' on both ''S'' and ''T''.
   *
   * @see [[https://en.wikipedia.org/wiki/Equivariant_map]]
   */
  def isEquivariant[S,T,G](f: S ⇒ T)(implicit α: Action[S,G], β:Action[T,G],γ :Arbitrary[S],δ:Arbitrary[G]): Unit =
  {
    forAll("s","g") {(s: S,g: G) ⇒
    {
      assert(f(s) + g == f(s + g),        "[equivarient]")
    }}
  }

  /**
   * Check that ''S'' satisfies the axioms of a partial ordering; namely that
   * the relation `<=` is reflexive, antisymmetric, and transitive.
   *
   * @see [[https://en.wikipedia.org/wiki/Partially_ordered_set]]
   */
  def isPartiallyOrdered[S <: Ordered[S]]()(implicit α:Arbitrary[S]) : Unit =
  {
    forAll("s","t","u") {(s: S,t: S,u: S) ⇒
    {
      assert(s<=s,                        "[reflexive]")
      assert(s<=t && t<=s implies s==t,   "[antisymmetric]")
      assert(s<=t && t<=u implies s<=u,   "[transitive]")
    }}
  }

  /**
   * Check that ''S'' satisfies the axioms of a total ordering; namely that it
   * is partially ordered and, moreover, that the ordering is total.
   *
   * @see [[https://en.wikipedia.org/wiki/Total_order]]
   */
  def isTotallyOrdered[S <: Ordered[S]]()(implicit α:Arbitrary[S]) : Unit =
  {
    isPartiallyOrdered[S]()

    forAll("s","t") {(s: S,t: S) ⇒
    {
      assert(s<=t || t<=s,                "[total]")
    }}
  }

  /**
   * Defines random generators for some of the core data types.
   */
  object arbitrary
  {
    def gen[α:Choose,β](l: α,h: α,f: α ⇒ β) = Arbitrary(choose(l,h) map f)

    implicit val α = gen(-128.0,128.0,(x: ℝ) ⇒ x)
    implicit val β = gen(   2.0, 10.0,(x: ℝ) ⇒ Hz(Math.exp(x)))
    implicit val γ = gen(   0,  128,  (x: ℕ) ⇒ Pitch(x))
    implicit val δ = gen(   0,  128,  (x: ℕ) ⇒ Note(Pitch(x)))
    implicit val ε = gen(   0,0xFFF,  (x: ℕ) ⇒ Notes(x))
  }
}

//****************************************************************************
