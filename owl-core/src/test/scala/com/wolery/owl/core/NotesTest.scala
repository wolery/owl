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

package com.wolery
package owl
package core

//****************************************************************************

class NotesTest extends CoreSuite
{
  import arbitrary._                                     // For Owl implicits

  test("Set[Note](⊆) is a partial ordering")
  {
    isPartiallyOrdered[Set[Note]]()                      // Verify the axioms
  }

  test("Set[Note] is a ℤ-set")
  {
    isℤSet[Set[Note]]()                                  // Verify the axioms
  }

  test("Notes(⊆) is a partial ordering")
  {
    isPartiallyOrdered[Notes]()                          // Verify the axioms
  }

  test("Notes is a ℤ-set")
  {
    isℤSet[Notes]()                                      // Verify the axioms
  }

  // @see  [[https://en.wikipedia.org/wiki/Boolean_algebra_(structure) Boolean
  //       algebra (Wikipedia)]]

  test("Notes(∅,~∅,~,∩,∪) is a boolean algebra")
  {
    val ⊤ =  Notes.full
    val ⊥ =  Notes.empty

    forAll("a","b","c") {(a: Notes,b: Notes,c: Notes) ⇒  // ∀ a,b,c ∈ Notes
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

  test("C major looks ok")
  {
    val Cmaj = Notes(C,D,E,F,G,A,B)                      // C ionian
    val Gmaj = Notes(G,A,B,C,D,E,F.♯)                    // G ionian

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
    assert(Cmaj ⊖ Gmaj == Notes(F,F.♯),                  "[⊖]")
    assert(~Cmaj == Notes(C.♯,D.♯,F.♯,G.♯,A.♯),          "[~]")
  }
}

//****************************************************************************
