//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : Unit tests for class Note.
//*
//*
//*  Comments: This file uses a tab size of 3 spaces.
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
   implicit val n = Arbitrary(oneOf(Note.notes))
   implicit val z = Arbitrary(choose(-128,128))

   test("Note is intervallic")
   {
      isIntervallic[Note]()
   }

   test("Note properties")
   {
      forAll("n") { (n : Note) ⇒
      {
         assert(n(4) == Pitch(n,4),          "pitch")
      }}
   }
}

//****************************************************************************
