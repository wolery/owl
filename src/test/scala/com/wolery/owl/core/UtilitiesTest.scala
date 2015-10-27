//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : Unit tests for object Utilities.
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*
//*
//****************************************************************************

package com.wolery.owl.core;

import org.scalacheck.Arbitrary
import org.scalacheck.Gen.choose
import org.scalatest.FunSuite

import CoreTest._
import utilities._

//****************************************************************************

class UtiltiesTest extends FunSuite
{
  test("mod12")
  {
    implicit val α = Arbitrary(choose(-1024,1024))

    for (i ← 0 to  11) {assert(mod12(i) ==  i)}
    for (i ← 0 to -11) {assert(mod12(i) == -i)}

    forAll("i","j") {(i: ℤ,j: ℤ) ⇒
    {
      val m = mod12(i)

      assert(0<=m && m<12)
      assert(m == mod12(i + 12*j))
    }}
  }

  test("rol12")
  {
    import Integer.bitCount
    implicit val α = Arbitrary(choose(0,0xFFF))

    forAll("i","by") {(i: ℤ,by: ℤ) ⇒
    {
      val j = rol12(i,by)

      assert(0<=j && j<=0xFFF)
      assert(j == rol12(i,by+12))
      assert(j == rol12(i,by-12))
      assert(i == ror12(rol12(i,by),by))
      assert(bitCount(j) == bitCount(i))
    }}
  }

  test("ror12")
  {
    import Integer.bitCount
    implicit val α = Arbitrary(choose(0,0xFFF))

    forAll("i","by") {(i: ℤ,by: ℤ) ⇒
    {
      val j = ror12(i,by)

      assert(0<=j && j<=0xFFF)
      assert(j == ror12(i,by+12))
      assert(j == ror12(i,by-12))
      assert(i == rol12(ror12(i,by),by))
      assert(bitCount(j) == bitCount(i))
    }}
  }
}

//****************************************************************************
