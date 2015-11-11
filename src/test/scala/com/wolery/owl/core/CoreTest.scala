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

//****************************************************************************

import org.scalacheck.Gen
import org.scalacheck.Gen._
import org.scalacheck.Arbitrary
import org.scalatest.Assertions._
import org.scalatest.FunSuite
import org.scalatest.prop.PropertyChecks

//****************************************************************************

class CoreTest extends FunSuite
{
  import CoreTest._

  /* ℝ only satisfies the axioms exactly for limited subsets of its values due
   	 to the inherent limitations of floating point representation. We are only
   	 interested in audible frequencies, however, so restrict the set of values
   	 that we test for rather than thread a  custom comparison function through
   	 the entire test suite...*/

  implicit val r = Arbitrary(generate.real)              // For r ∈ [-128,128]

  test("ℤ is a ℤ-torsor")                 {isTorsor[ℤ,ℤ]()}
  test("ℤ is abelian")                    {isCommutative[ℤ]()}
  test("ℝ is an R-torsor")                {isTorsor[ℝ,ℝ]()}
  test("ℝ is abelian")                    {isCommutative[ℝ]()}
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
   * Check that ''G'' satisfies the axioms of a group; namely that it is a
   * monoid in which every element possesses an additive inverse.
   *
   * @see [[https://en.wikipedia.org/wiki/Group_(mathematics)]]
   */
  def isGroup[G]()(implicit α: Group[G],β: Arbitrary[G]): Unit =
  {
    isMonoid[G]()                         // group ⇒ monoid

    forAll("g") {(g: G) ⇒
    {
      assert(g + -g == α.zero,            "[negation]")
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
    isGroup[G]()                          // action ⇒ group

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
   * @see [[https://en.wikipedia.org/wiki/Principal_homogeneous_space]]
   */
  def isTorsor[S,G]()(implicit α: Torsor[S,G],β: Arbitrary[S],γ: Arbitrary[G]) : Unit =
  {
    isAction[S,G]()                       // torsor ⇒ action

    forAll("s","t") {(s: S,t: S) ⇒
    {
      assert(s + (t - s) == t,            "[interval]")
    }}
  }

  /**
   * Check that ''S'' satisfies the axioms of a transposing set; namely that
   * it is a ℤ-set.
   */
  def isTransposing[S]()(implicit α: Transposing[S],β: Arbitrary[S],γ: Arbitrary[ℤ]) : Unit =
  {
    isAction[S,ℤ]()                       // transposing ⇒ ℤ-action
  }

  /**
   * Check that ''S'' satisfies the axioms of an intervallic set; namely, that
   * it is a transposing ℤ-torsor.
   */
  def isIntervallic[S]()(implicit α: Intervallic[S],β: Arbitrary[S],γ: Arbitrary[ℤ]) : Unit =
  {
    isTransposing[S]()                    // intervallic ⇒ transposing
    isTorsor[S,ℤ]()                       // intervallic ⇒ ℤ-torsor

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
  def isEquivariant[S,T,G](f: S ⇒ T)(implicit α: Action[S,G], β:Action[T,G],γ : Arbitrary[S],δ: Arbitrary[T],ε: Arbitrary[G]): Unit =
  {
    isAction[S,G]()                       // equivariant ⇒ G-action
    isAction[T,G]()                       // equivariant ⇒ G-action

    forAll("s","g") {(s: S,g: G) ⇒
    {
      assert(f(s) + g == f(s + g),        "[equivarient]")
    }}
  }

  /**
   * Check that ''S'' satisfies the axioms of a partial ordering; namely that
   * the relation <= is reflexive, antisymmetric, and transitive.
   *
   * @see [[https://en.wikipedia.org/wiki/Partially_ordered_set]]
   */
  def isPartiallyOrdered[S]()(implicit α: PartialOrdering[S],β: Arbitrary[S]) : Unit =
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
   * is partially ordered and, moreover, that the ordering <= is total.
   *
   * @see [[https://en.wikipedia.org/wiki/Total_order]]
   */
  def isOrdered[S]()(implicit α: PartialOrdering[S],β: Arbitrary[S]) : Unit =
  {
    isPartiallyOrdered[S]()               // ordered ⇒ partially ordered

    forAll("s","t") {(s: S,t: S) ⇒
    {
      assert(s<=t || t<=s,                "[total]")
    }}
  }

  /**
   * Defines generators for many of the core data types.
   */
  object generate
  {
    def gen[α: Choose,β](l: α,h: α,f: α ⇒ β): Gen[β] = choose(l,h) map f

    val int   = gen(-128  ,  128  ,(x: ℤ) ⇒ x)
    val real  = gen(-128.0,  128.0,(x: ℝ) ⇒ x)
    val hertz = gen(   2.0,   10.0,(x: ℝ) ⇒ Hz(Math.exp(x)))
    val pitch = gen(   0  ,  128  ,(x: ℕ) ⇒ Pitch(x))
    val note  = gen(   0  ,  128  ,(x: ℕ) ⇒ Note(Pitch(x)))
    val notes = gen(   0  ,0xFFF  ,(x: ℕ) ⇒ Notes(x))
    val shape = gen(   0  ,0xFFF  ,(x: ℕ) ⇒ Shape(x | 1))
    val scale = for(r ←note;s ←shape) yield Scale(r,s)
  }

  /**
   * Defines implicit random generators for many of the core data types.
   */
  object arbitrary
  {
    implicit val α = Arbitrary(generate.hertz)
    implicit val β = Arbitrary(generate.pitch)
    implicit val γ = Arbitrary(generate.note)
    implicit val δ = Arbitrary(generate.notes)
    implicit val ε = Arbitrary(generate.shape)
    implicit val ζ = Arbitrary(generate.scale)
  }
}

//****************************************************************************
