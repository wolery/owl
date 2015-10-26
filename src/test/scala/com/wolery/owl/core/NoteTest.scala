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

import org.scalacheck.Arbitrary
import org.scalacheck.Gen.oneOf
import org.scalacheck.Gen.choose
import org.scalatest.FunSuite

import CoreTest._

//****************************************************************************

class NoteTest extends FunSuite
{
  implicit val α = Arbitrary(oneOf(Note.notes))
  implicit val β = Arbitrary(choose(-128,128))

  test("Note is intervallic")
  {
    isIntervallic[Note]()
  }

  test("Note properties are consistent")
  {
    forAll("n") {(n: Note) ⇒
    {
      for (o ← -1 to 9)
      {
        assert(n(o) == Pitch(n,o),  "octave")
      }
    }}
  }
}

//****************************************************************************
