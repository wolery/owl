//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : Unit tests for the owl package object.
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*
//*
//****************************************************************************

package com.wolery.owl

//****************************************************************************

class OwlTest extends OwlSuite
{
  test("iff")
  {
    assert(true  iff true,        "[1]")
    reject(true  iff false,       "[2]")
    reject(false iff true,        "[3]")
    assert(false iff false,       "[4]")
  }

  test("implies")
  {
    assert(true  implies true,    "[1]")
    reject(true  implies false,   "[2]")
    assert(false implies true,    "[3]")
    assert(false implies false,   "[4]")
  }

  test("min")
  {
    assert(0  .min(1)   == 0,     "[1]")
    assert(0.0.min(1.0) == 0.0,   "[2]")
    assert('a'.min('b') == 'a',   "[3]")
  }

  test("max")
  {
    assert(0  .max(1)   == 1,     "[1]")
    assert(0.0.max(1.0) == 1.0,   "[2]")
    assert('a'.max('b') == 'b',   "[3]")
  }

  test("SetEx")
  {
    val s = Set(1,2,3)
    val t = Set(  2,3,4)

    assert(∅[ℕ].isEmpty,          "[∅[ℕ]]")
    assert(∅[ℤ].isEmpty,          "[∅[ℤ]]")
    assert(∅[ℝ].isEmpty,          "[∅[ℝ]]")

    assert(1 ∈ s,                 "[∈]")
    assert(1 ∉ t,                 "[∉]")
    assert(s ∋ 1,                 "[∋]")
    assert(t ∌ 1,                 "[∌]")

    assert(s \ t == Set(1),       "[\\]")
    assert(s ∪ t == Set(1,2,3,4), "[∪]")
    assert(s ∩ t == Set(2,3),     "[∩]")
    assert(s ⊖ t == Set(1,4),     "[⊖]")
    reject(s ⊂ t,                 "[⊂]")
    reject(s ⊃ t,                 "[⊃]")
    assert(s ⊄ t,                 "[⊄]")
    assert(s ⊅ t,                 "[⊅]")
    reject(s ⊆ t,                 "[⊆]")
    reject(s ⊇ t,                 "[⊇]")
    assert(s ⊈ t,                 "[⊈]")
    assert(s ⊉ t,                 "[⊉]")
  }
}

//****************************************************************************
