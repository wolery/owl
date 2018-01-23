//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : Custom test suite for testing the core package.
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*                                                                     0-0
//*                                                                   (| v |)
//**********************************************************************w*w***

package com.wolery.owl
package core

//****************************************************************************

import org.scalacheck._
import org.scalacheck.Gen._

//****************************************************************************

trait CoreSuite extends OwlSuite
{
  /**
   * Checks that `S` satisfies the axioms for a [[Finite]] type;  that is, the
   * mappings `toℕ` and `fromℕ` are both injective and mutually inverse,  that
   * the `S` is non empty, and that the range of `toℕ` is contained within the
   * interval `[0, size)`.
   */
  def isFinite[S]()(implicit α: Finite[S],β: Arbitrary[S]): Unit =
  {
    assert(α.size > 0,                                 "[S is inhabited]")

    forAll("s") {(s: S) ⇒                              // ∀ s ∈ S
    {
      assert(s.toℕ.isBetween(0,α.size - 1),            "[0 ≤ toℕ < size]")
      assert(s.toℕ.fromℕ == s,                         "[toℕ ∘ fromℕ = id]")
    }}
  }

  /**
   * Checks that `(S,⋅)` satisfies the axioms of a [[Semigroup]]; that is, the
   * mapping `⋅ : S → S` is associative.
   *
   * @see    [[https://en.wikipedia.org/wiki/Semigroup Semigroup (Wikipedia)]]
   */
  def isSemigroup[S]()(implicit α: Semigroup[S],β: Arbitrary[S]): Unit =
  {
    forAll("a","b","c") {(a: S,b: S,c: S) ⇒              // ∀ a,b,c ∈ S
    {
      assert(a ⋅ (b ⋅ c) == (a ⋅ b) ⋅ c,                 "[associativity]")
    }}
  }

  /**
   * Checks that `(S,⋅)` satisfies the axioms of a commutative magma; that is,
   * the mapping `⋅ : S → S` is commutative.
   *
   * @see    [[https://en.wikipedia.org/wiki/Commutative_property
   *         Commutativity (Wikipedia)]]
   */
  def isCommutative[S]()(implicit α: Semigroup[S],β: Arbitrary[S]): Unit =
  {
    forAll("a","b") {(a: S,b: S) ⇒                       // ∀ a,b ∈ S
    {
      assert(a ⋅ b == b ⋅ a,                             "[commutativity]")
    }}
  }

  /**
   * Checks that `(M, ⋅, e)` satisfies the axioms of a [[Monoid]]; that is, it
   * is a [[Semigroup]] with a two sided identity element `e`.
   *
   * @see    [[https://en.wikipedia.org/wiki/Monoid Monoid (Wikipedia)]]
   */
  def isMonoid[M]()(implicit α: Monoid[M],β: Arbitrary[M]): Unit =
  {
    import α._                                           // For identity e

    isSemigroup[M]()                                     // Semigroup axioms

    forAll("a") {(m: M) ⇒                                // ∀ m ∈ M
    {
      assert(e ⋅ m == m,                                 "[left identity]")
      assert(m ⋅ e == m,                                 "[right identity]")
    }}
  }

  /**
   * Checks that `(G, ⋅, e)` satisfies the axioms of a [[Group]];  that is, it
   * is a [[Monoid]] in which every element possesses an inverse element.
   *
   * @see    [[https://en.wikipedia.org/wiki/Group_(mathematics) Group
   *         (Wikipedia)]]
   */
  def isGroup[G]()(implicit α: Group[G],β: Arbitrary[G]): Unit =
  {
    import α._                                           // For identity e

    isMonoid[G]()                                        // Monoid axioms

    forAll("g") {(g: G) ⇒                                // ∀ g ∈ G
    {
      assert(-g ⋅  g == e,                               "[left inverse]")
      assert( g ⋅ -g == e,                               "[right inverse]")
    }}
  }

  /**
   * Checks that `G` acts upon the carrier set `S`; that is, `apply` effects a
   * group isomorphism from `G` into `Sym(S)`.
   *
   * @see    [[https://en.wikipedia.org/wiki/Group_action Group action
   *         (Wikipedia)]]
   */
  def isAction[S,G]()(implicit α: Action[S,G],β: Arbitrary[S],γ: Arbitrary[G]): Unit =
  {
    import α._                                           // For action ops
    import α.group._                                     // For identity e

    isGroup[G]()                                         // Group axioms

    forAll("s") {(s: S) ⇒                                // ∀ s ∈ S
    {
      assert(s + e == s,                                 "[identity]")
    }}

    forAll("s","f","g") {(s: S,f: G,g: G) ⇒              // ∀ s ∈ S, f,g ∈ G
    {
      assert(s + (f ⋅ g) == (s + f) + g,                 "[compatability +]")
      assert(s - (f ⋅ g) == (s - g) - f,                 "[compatability -]")
      assert(s + f       == s - -f,                      "[negation +]")
      assert(s - f       == s + -f,                      "[negation -]")
    }}
  }

  /**
   * Checks that `G` acts regularly upon the carrier set `S`; that is, `apply`
   * effects a group isomorphism from `G` into `Sym(S)`.
   *
   * @see    [[https://en.wikipedia.org/wiki/Principal_homogeneous_space
   *         Torsor (Wikipedia)]]
   */
  def isTorsor[S,G]()(implicit α: Torsor[S,G],β: Arbitrary[S],γ: Arbitrary[G]): Unit =
  {
    import α._                                           // For action ops

    isAction[S,G]()                                      // Action axioms

    forAll("s","t") {(s: S,t: S) ⇒                       // ∀ s,t ∈ S
    {
      assert(s + delta(s,t) == t,                        "[transitive]")
      assert(s + (t - s)    == t,                        "[transitive]")
    }}
  }

  /**
   * Checks that `ℤ` acts upon the carrier set `S`; that is, `apply` effects a
   * group homomorphism from `ℤ into `Sym(S)`.
   */
  def isℤSet[S]()(implicit α: Action[S,ℤ],β: Arbitrary[S],γ: Arbitrary[ℤ]): Unit =
  {
    isAction[S,ℤ]()                                      // Action axioms

    forAll("s") {(s: S) ⇒                                // ∀ s ∈ S
    {
      assert(s.♭ == s - 1,                               "[flat]")
      assert(s.♮ == s    ,                               "[natural]")
      assert(s.♯ == s + 1,                               "[sharp]")
    }}
  }

  /**
   * Checks that `ℤ` acts regularly upon the carrier set `S`; that is, `apply`
   * effects a group isomorphism from `ℤ into `Sym(S)`.
   */
  def isℤTorsor[S]()(implicit α: Torsor[S,ℤ],β: Arbitrary[S],γ: Arbitrary[ℤ]): Unit =
  {
    isTorsor[S,ℤ]()                                      // Torsor axioms

    forAll("s") {(s: S) ⇒                                // ∀ s ∈ S
    {
      assert(s.♭ == s - 1,                               "[flat]")
      assert(s.♮ == s    ,                               "[natural]")
      assert(s.♯ == s + 1,                               "[sharp]")
    }}
  }

  /**
   * Checks that `f: S ⇒ T` is equivariant with respect to the actions of `G`
   * upon `S` and `T`; that is, it commutes with the action of `G` on both `S`
   * and `T`.
   *
   * @see    [[https://en.wikipedia.org/wiki/Equivariant_map Equivarient map
   *         (Wikipedia)]]
   */
  def isEquivariant[S,T,G](f: S ⇒ T)(implicit α: Action[S,G],β: Action[T,G],γ: Arbitrary[S],δ: Arbitrary[T],ε: Arbitrary[G]): Unit =
  {
    isAction[S,G]()                                      // G acts upon S
    isAction[T,G]()                                      // G acts upon T

    forAll("s","g") {(s: S,g: G) ⇒                       // ∀ s ∈ S, g ∈ G
    {
      assert(f(s) + g == f(s + g),                       "[equivarient]")
    }}
  }

  /**
   * Checks that `S` satisfies the axioms of a partial ordering; that is, the
   * relation `<=` is ''reflexive'', ''antisymmetric', and ''transitive''.
   *
   * @see    [[https://en.wikipedia.org/wiki/Partially_ordered_set Partial
   *         order (Wikipedia)]]
   */
  def isPartiallyOrdered[S]()(implicit α: PartialOrdering[S],β: Arbitrary[S]): Unit =
  {
    forAll("s","t","u") {(s: S,t: S,u: S) ⇒              // ∀ s,t,u ∈ S
    {
      assert(s<=s,                                       "[reflexive]")
      assert(s<=t && t<=s implies s==t,                  "[antisymmetric]")
      assert(s<=t && t<=u implies s<=u,                  "[transitive]")
    }}
  }

  /**
   * Checks that `S` satisfies the axioms of a total ordering; namely that it
   * is partially ordered and, moreover, that the ordering `<=` is ''total''.
   *
   * @see [[https://en.wikipedia.org/wiki/Total_order Total order (Wikipedia)]]
   */
  def isOrdered[S]()(implicit α: PartialOrdering[S],β: Arbitrary[S]): Unit =
  {
    isPartiallyOrdered[S]()                              // Check the axioms

    forAll("s","t") {(s: S,t: S) ⇒                       // ∀ s,t ∈ S
    {
      assert(s<=t || t<=s,                               "[total]")
    }}
  }

  /**
   * Defines generators for many of the core data types.
   *
   * Notice in particular the bounds we place upon integers and real numbers:
   * ℝ and ℤ only satisfy the torsor axioms exactly for limited subsets of the
   * total possible range of  representable values due to inherent limitations
   * of the floating point representation.  We are only  interested in audible
   * frequencies and pitches,  however,  so restrict the set of values that we
   * test over rather than thread custom comparison functions through the test
   * suite.
   */
  object generate
  {
    def gen[α: Choose,β](l: α,h: α,f: α ⇒ β): Gen[β] = choose(l,h) map f

    val int   = gen(-4096,  4096, (x: ℤ) ⇒ x)
    val real  = gen(-4096,  4096, (x: ℤ) ⇒ x * 1.0)
    val hertz = gen(    2,    10, (x: ℕ) ⇒ Hz(Math.exp(x)))
    val pitch = gen(    0,   127, (x: ℕ) ⇒ Pitch(x))
    val note  = gen(    0,   127, (x: ℕ) ⇒ Note(Pitch(x)))
    val notes = gen(    0, 0xFFF, (x: ℕ) ⇒ Notes.fromBitMask(x))
    val shape = gen(    0, 0xFFF, (x: ℕ) ⇒ Shape(x | 1))
    val scale = for(r ←note;s ←shape) yield Scale(r,s)
  }

  /**
   * Defines implicit random generators for many of the core data types.
   */
  object arbitrary
  {
    implicit val α = Arbitrary(generate.int)
    implicit val β = Arbitrary(generate.real)
    implicit val γ = Arbitrary(generate.hertz)
    implicit val δ = Arbitrary(generate.pitch)
    implicit val ε = Arbitrary(generate.note)
    implicit val ζ = Arbitrary(generate.notes)
    implicit val η = Arbitrary(generate.shape)
    implicit val θ = Arbitrary(generate.scale)
  }
}

//****************************************************************************
