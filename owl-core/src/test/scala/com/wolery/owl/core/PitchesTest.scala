//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : Unit tests for class Pitches.
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*                                                                     0-0
//*                                                                   (| v |)
//**********************************************************************w*w***

package com.wolery.owl
package core

//****************************************************************************

class PitchesTest extends CoreSuite
{
  import arbitrary._                                     // For Owl implicits

  test("Set[Pitch] is a ℤ-set")
  {
    assertℤSet[Set[Pitch]]()                             // Verify the axioms
  }

  test("Pitches(⊆) is a partial ordering")
  {
    assertPartiallyOrdered[Pitches]()                    // Verify the axioms
  }

  // @see  [[https://en.wikipedia.org/wiki/Boolean_algebra_(structure) Boolean
  //       algebra (Wikipedia)]]

  test("Pitches(∅,~∅,~,∩,∪) is a boolean algebra")
  {
    val ⊤ =  Pitches.full
    val ⊥ =  Pitches.empty

    forAll("a","b","c"){(a: Pitches,b: Pitches,c: Pitches)⇒// ∀ a,b,c ∈ Pitches
    {
      assert(a ∪ (b ∪ c)  == (a ∪ b) ∪ c,                "[∪ associativity]")
      assert(a ∩ (b ∩ c)  == (a ∩ b) ∩ c,                "[∩ associativity]")
      assert(     a ∪ b   ==  b ∪ a,                     "[∪ commutativity]")
      assert(     a ∩ b   ==  b ∩ a,                     "[∩ commutativity]")
      assert(a ∪ (a ∩ b)  == a,                          "[∪ absorption]")
      assert(a ∩ (a ∪ b)  == a,                          "[∩ absorption]")
      assert(     a ∪ ⊥   ==  a,                         "[∪ identity]")
      assert(     a ∩ ⊤   ==  a,                         "[∩ identity]")
      assert(a ∪ (b ∩ c)  == (a ∪ b) ∩ (a ∪ c),          "[∪ distributivity]")
      assert(a ∩ (b ∪ c)  == (a ∩ b) ∪ (a ∩ c),          "[∩ distributivity]")
      assert(     a ∪ ~a  == ⊤,                          "[∪ complements]")
      assert(     a ∩ ~a  == ⊥,                          "[∩ complements]")
    }}
  }
}

//****************************************************************************
