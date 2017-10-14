//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : Unit tests for class Note.
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*
//*
//****************************************************************************

package com.wolery.owl.core;

//****************************************************************************

class NoteTest extends CoreSuite
{
  import arbitrary._                                     // For owl implicits

  implicit val i = Arbitrary(generate.int)               // For i ∈ [-128,128]

  test("Note is intervallic")
  {
    isIntervallic[Note]()                                // Verify the axioms
  }

  test("Note invariants")
  {
    forAll("n") {(n: Note) ⇒
    {
      assert(n == n,                                     "[n = n]")
      assert(n != n + 1,                                 "[n != n + 1]")
      assert(n != n - 1,                                 "[n != n - 1]")
      assert(n == n + 12,                                "[n = n + 12]")
      assert(n == n - 12,                                "[n = n - 12]")

      for (o ← -1 to 9) {assert(n(o) == Pitch(n,o),      "[n(o) = Pitch(n,o)")}
    }}

    assert(Set(C,D,E,F,G,A,B).size == 7,                 "[7 white notes]")
  }
}

//****************************************************************************