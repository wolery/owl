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

//****************************************************************************

import org.scalacheck.Arbitrary
import org.scalacheck.Gen.choose
import org.scalatest.FunSuite
import org.scalatest.prop.PropertyChecks

//****************************************************************************

class UtiltiesTest extends FunSuite with PropertyChecks
{
  import Integer.bitCount
  import utilities._

  test("mod12")
  {
    for (i ← 0 to  11) {assert(mod12(i) ==  i)}
    for (i ← 0 to -11) {assert(mod12(i) == -i)}

    forAll("i") {(i: ℤ) ⇒
    {
      val r = mod12(i)

      assert(0<=i implies i == r + 12 * (i/12))
      assert(0<=r && r<12)
    }}
  }

  test("rol12")
  {
    assert(rol12(0x001, 1) == 0x002,"A")
    assert(rol12(0x001,23) == 0x800,"B")
    assert(rol12(0x800, 1) == 0x001,"C")
    assert(rol12(0x800,-4) == 0x080,"D")

    implicit val α = Arbitrary(choose(0,0xFFF))

    forAll("i","by") {(i: ℤ,by: ℤ) ⇒
    {
      val j = rol12(i,by)

      assert(0<=j && j<=0xFFF)
      assert(j == rol12(i,by+12))
      assert(j == rol12(i,by-12))
      assert(i == ror12(rol12(i,by),by))
      assert(bitCount(i) == bitCount(j))
    }}
  }

  test("ror12")
  {
    assert(ror12(0x001, 1) == 0x800,"A")
    assert(ror12(0x001,25) == 0x800,"B")
    assert(ror12(0x100, 1) == 0x080,"C")
    assert(ror12(0x800,-4) == 0x008,"D")

    implicit val α = Arbitrary(choose(0,0xFFF))

    forAll("i","by") {(i: ℤ,by: ℤ) ⇒
    {
      val j = ror12(i,by)

      assert(0<=j && j<=0xFFF)
      assert(j == ror12(i,by+12))
      assert(j == ror12(i,by-12))
      assert(i == rol12(ror12(i,by),by))
      assert(bitCount(i) == bitCount(j))
    }}
  }
}

//****************************************************************************
