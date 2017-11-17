//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : Unit tests for class Notes.
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*                                                                     0-0
//*                                                                   (| v |)
//**********************************************************************w*w***

package com.wolery.owl
package core

//****************************************************************************

class NotesTest extends CoreSuite
{
  import arbitrary._                                     // For owl implicits

  test("Notes is a ℤ-set")
  {
    implicit val i = Arbitrary(generate.int)             // For i ∈ [-128,128]

    isℤSet[Notes]()                                      // Verify the axioms
  }

  test("Notes(⊆) is a partial ordering")
  {
    implicit object α extends PartialOrdering[Notes]
    {
      def lteq(s: Notes,t: Notes)       = s ⊆ t
      def tryCompare(x: Notes,y: Notes) = ???
    }

    isPartiallyOrdered[Notes]()                          // Verify the axioms
  }

  // @see [[https://en.wikipedia.org/wiki/Boolean_algebra_(structure) Boolean algebra (Wikipedia)]]

  test("Notes(∅,~∅,~,∩,∪) is a boolean algebra")
  {
    val (nil,one) = (Notes(),~Notes())

    forAll("a","b","c") {(a: Notes,b: Notes,c: Notes) =>
    {
      assert(a ∪ (b ∪ c)  == (a ∪ b) ∪ c,                "[∪ associativity]")
      assert(a ∩ (b ∩ c)  == (a ∩ b) ∩ c,                "[∩ associativity]")
      assert(     a ∪ b   ==  b ∪ a,                     "[∪ commutativity]")
      assert(     a ∩ b   ==  b ∩ a,                     "[∩ commutativity]")
      assert(a ∪ (a ∩ b)  == a,                          "[∪ absorption]")
      assert(a ∩ (a ∪ b)  == a,                          "[∩ absorption]")
      assert(     a ∪ nil ==  a,                         "[∪ identity]")
      assert(     a ∩ one ==  a,                         "[∩ identity]")
      assert(a ∪ (b ∩ c)  == (a ∪ b) ∩ (a ∪ c),          "[∪ distributivity]")
      assert(a ∩ (b ∪ c)  == (a ∩ b) ∪ (a ∩ c),          "[∩ distributivity]")
      assert(     a ∪ ~a  == one,                        "[∪ complements]")
      assert(     a ∩ ~a  == nil,                        "[∩ complements]")
    }}
  }

  test("C major looks ok")
  {
    import scala.language.postfixOps

    val Cmaj = Notes(C,D,E,F,G,A,B)
    val Gmaj = Notes(G,A,B,C,D,E,F♯)

    assert(Cmaj.size == 7,                               "[size = 7]")

    assert(C   ∈ Cmaj,                                   "[C ]")
    assert(C.♯ ∉ Cmaj,                                   "[C♯]")
    assert(D   ∈ Cmaj,                                   "[D ]")
    assert(D.♯ ∉ Cmaj,                                   "[D♯]")
    assert(E   ∈ Cmaj,                                   "[E ]")
    assert(F   ∈ Cmaj,                                   "[F ]")
    assert(F.♯ ∉ Cmaj,                                   "[F♯]")
    assert(G   ∈ Cmaj,                                   "[G ]")
    assert(G.♯ ∉ Cmaj,                                   "[G♯]")
    assert(A   ∈ Cmaj,                                   "[A ]")
    assert(A.♯ ∉ Cmaj,                                   "[A♯]")
    assert(B   ∈ Cmaj,                                   "[B ]")

    assert(Cmaj+7 == Gmaj,                               "[Gmaj]")

    assert(Cmaj \ Gmaj == Notes(F),                      "[\\]")
    assert(Cmaj ∪ Gmaj == Cmaj + F.♯,                    "[∪]")
    assert(Cmaj ∩ Gmaj == Cmaj - F,                      "[∩]")
    assert(Cmaj ⊖ Gmaj == Notes(F,F♯),                   "[⊖]")
    assert(~Cmaj == Notes(C♯,D♯,F♯,G♯,A♯),               "[~]")
  }
}

//****************************************************************************
