//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : Unit tests for class Pitch.
//*
//*
//*  Comments: This file uses a tab size of 3 spaces.
//*
//*
//****************************************************************************

package com.wolery.owl.core;

import org.scalacheck.Arbitrary
import org.scalacheck.Gen.choose
import org.scalatest.FunSuite

import CoreTest._

//****************************************************************************

class PitchTest extends FunSuite
{
   implicit val p = Arbitrary(for {i ← choose(0,128)} yield Pitch(i))
   implicit val z = Arbitrary(choose(-128,128))

   test("Pitch is intervallic")
   {
      isIntervallic[Pitch]()
   }

   test("Pitch is ordered")
   {
      isOrdered[Pitch]()
   }

   test("Pitch⇒Note is equivarient")
   {
      isEquivariant[Pitch,Note,ℤ](_.note)
   }

   test("Pitch properties")
   {
      forAll("p") { (p:Pitch) ⇒
      {
         assert(p == Pitch(p.midi),          "midi")
         assert(p == Pitch(p.frequency),     "frequency")
         assert(p == Pitch(p.note,p.octave), "note+octave")
      }}
   }
}

//****************************************************************************
