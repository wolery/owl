//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose :
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*                                                                     0-0
//*                                                                   (| v |)
//**********************************************************************w*w***

package com.wolery.owl
package core

//****************************************************************************

import util.utilities.isPowerOf2

//****************************************************************************

case class Meter(beats: ℕ = 4,symbol: ℕ = 4,clocks: ℕ = 24,subbeats: ℕ = 8)
{
  assert(beats>0 && isPowerOf2(symbol) && clocks>0 && subbeats>0)

  override
  def toString: String = s"$beats / $symbol"
}

//****************************************************************************
