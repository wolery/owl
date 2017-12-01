//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : Unit tests for class Pitch.
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*                                                                     0-0
//*                                                                   (| v |)
//**********************************************************************w*w***

package com.wolery.owl
package core

//****************************************************************************

class PitchTest extends CoreSuite
{
  import arbitrary._                                     // For Owl implicits

  test("Pitch is a ℤ/128ℤ-torsor")
  {
    isℤTorsor[Pitch]()                                   // Verify the axioms
  }

  test("Pitch is totally ordered")
  {
    isOrdered[Pitch]()                                   // Verify the axioms
  }

  test("Pitch ⇒ Note is equivarient")
  {
    isEquivariant[Pitch,Note,ℤ](_.note)                  // Verify the axioms
  }

  test("Pitch construction")
  {
    forAll("p") {(p: Pitch) ⇒                            // ∀ p ∈ Pitch
    {
      assert(p == Pitch(p.midi),                         "[Midi]")
      assert(p == Pitch(p.frequency),                    "[Frequency]")
      assert(p == Pitch(p.note,p.octave),                "[Note,Octave]")
    }}
  }

  test("Pitch reference")
  {
    assert(A(4).midi      == 69,                         "[A4]")
    assert(A(4).frequency == Hz(440.0),                  "[A440]")
  }
}

//****************************************************************************
