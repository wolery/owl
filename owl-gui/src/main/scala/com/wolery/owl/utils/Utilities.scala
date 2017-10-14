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
//*
//*
//****************************************************************************

package com.wolery.owl
package utils

//****************************************************************************

object utilities
{
  /**
   * Clamp the given value to lie within the closed interval [''lo'', ''hi''].
   *
   * @param lo  The lower bound of the range to which ''v'' is clamped.
   * @param v   The value to be clamped.
   * @param hi  The upper bound of the range to which ''v'' is clamped.
   *
   * @return The value ''v'', clamped to lie within the closed interval
   *         [''lo'', ''hi''].
   */
  def clamp[α: Ordering](lo: α,v: α,hi: α): α =
  {
    import Ordering.Implicits._                          // For comparison ops

    assert(lo <= hi);                                    // Validate arguments

    if (v < lo)                                          // Less than 'lo'?
      lo                                                 // ...clamp to 'lo'
    else
    if (v > hi)                                          // Greater than 'hi'?
      hi                                                 // ...clamp to 'hi'
    else                                                 // Within interval
      v                                                  // ...nothing to do
  }
}

//****************************************************************************
