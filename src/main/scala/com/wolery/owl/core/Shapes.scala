//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : Provides additional information about scale shapes.
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*
//*
//****************************************************************************

package com.wolery.owl.core

//****************************************************************************

import scala.collection.mutable.Map

/**
 * Provides additional information about scale shapes beyond that which can be
 * calculated from their underlying interval structures.
 *
 * Implements a tiny database of meta data about shapes as a pair of maps from
 * names and shapes to instances of class [[Shapes.Info]].
 */
//private[core]
object Shapes
{
  /**
   * Augments a scale shape with information that cannot be computed from its
   * interval structure alone, including:
   *
   *  - the preferred name for the shape
   *  - other names by which the shape is commonly known
   *
   * @param shape The shape for which we carry additional information.
   * @param names A ':' delimited list of names by which the shape is commonly
   * 							known, beginning with its preferred name.
   */
  final class Info (val shape: Shape,_names: String)
  {
    /**
     * The preferred name for this shape.
     */
    def name: Name = _names.split(":",2).head

    /**
     * A list of names by which this shape is commonly known, beginning with
     * its preferred name.
     */
    def names: Seq[Name] = _names.split(":")

    @deprecated def aliases: Seq[Name] = names
  }

  /**
   * Returns additional information about the scale shape with the given name.
   *
   * Not every shape ''has'' such additional information - there are thousands
   * of them, after all - so we return the result in the Maybe monad.
   *
   * @param name The name by which a scale shape is commonly known.
   */
  def info(name: Name): Maybe[Info] = byNames.get(name.toLowerCase)

  /**
   * Returns additional information about the given scale shape.
   *
   * Not every shape ''has'' such additional information - there are thousands
   * of them, after all - so we return the result in the Maybe monad.
   *
   * @param shape A scale shape.
   */
  def info(shape: Shape): Maybe[Info] = byShape.get(shape)

  /**
   * Adds a scale shape to our little database.
   *
   * The new shape is specified via a sequence of intervals (that is, integers
   * modulo 12) that is either:
   *
   *  - ''absolute'': each interval is specified relative to the root of the
   *  								scale (e.g. `[0,2,4,5,7,9,11]` = diatonic)
   *  - ''relative'': each interval is specified relative to its predecessor
   *  								in the sequence (e.g. `[2,2,1,2,2,2,1]` = diatonic)
   *
	 * @param names     A ':' delimited list of names by which the scale shape
	 * 									is commonly known, beginning with the preferred name.
	 * @param intervals The sequence of intervals that specifies the underlying
	 * 									interval structure of the scale shape.
	 */
  private def f(names: String,intervals: ℤ*) =
  {
    assert(names.nonEmpty)                               // At least one name

    val s = Shape(intervals: _*)                         // The new shape
    val i = new Info(s,names)                            // The new shape info

    def add[Key](map: Map[Key,Info],key:Key) =           // Add key → i to map
    {
      assert(!map.contains(key))                         // ...not yet added
      map += key → i                                     // ...add it now
      assert( map.contains(key))                         // ...now in there
    }

    for (name <- names.split(":"))                       // For each name
    {
      add(byNames,name.toLowerCase)                      // Index by name
    }

    add(byShape,i.shape)                                 // Index by shape
  }

  private val byNames: Map[Name, Info] = Map.empty       // Indexed by name
  private val byShape: Map[Shape,Info] = Map.empty       // Indexed by shape

//****************************************************************************
// @see [[https://en.wikipedia.org/wiki/Jazz_scale Jazz scale (Wikipepdia)]]

  f("chromatic",                                         1,1,1,1,1,1,1,1,1,1,1,1)

// symmetric diminished

  f("whole half",                                        2,1,2,1,2,1,2,1)
  f("half whole",                                        1,2,1,2,1,2,1,2)

// diatonic major

  f("ionian:major:diatonic",                             2,2,1,2,2,2,1)
  f("dorian",                                            2,1,2,2,2,1,2)
  f("phrygian",                                          1,2,2,2,1,2,2)
  f("lydian",                                            2,2,2,1,2,2,1)
  f("myxolydian",                                        2,2,1,2,2,1,2)
  f("aeolian:minor:natural minor",                       2,1,2,2,1,2,2)
  f("locrian",                                           1,2,2,1,2,2,2)

// melodic minor

  f("melodic:melodic minor",                             2,1,2,2,2,2,1)
  f("dorian ♭2:phrygian ♮6)",                            1,2,2,2,2,1,2)
  f("lydian ♯5:lydian augmented:super lydian:acoustic",  2,2,2,2,1,2,1)
  f("lydian ♭7:lydian dominant:mixolydian ♯4",           2,2,2,1,2,1,2)
  f("mixolydian ♭6:melodic major",                       2,2,1,2,1,2,2)
  f("dorian ♭5:locrian ♮2:half diminished",              2,1,2,1,2,2,2)
  f("altered:altered dominant:super locrian",            1,2,1,2,2,2,2)

// harmonic minor
// @see [[http://docs.solfege.org/3.22/C/scales/har.html Harmonic minor]]

  f("harmonic:harmonic minor",                           2,1,2,2,1,3,1)
  f("locrian ♯6",                                        1,2,2,1,3,1,2)
  f("ionian ♯5:ionian augmented",                        2,2,1,3,1,2,1)
  f("dorian ♯4:romanian",                                2,1,3,1,2,1,2)
  f("phrygian ♯3:phrygian dominant",                     1,3,1,2,1,2,2)
  f("lydian ♯2",                                         3,1,2,1,2,2,1)
  f("myxolydian ♯1:ultra locrian",                       1,2,1,2,2,1,3)

// whole tone

  f("whole tone",                                        2,2,2,2,2,2)

// pentatonic
// @see [[https://en.wikipedia.org/wiki/Pentatonic_scale Pentatonic scale (Wikipedia)]]

  f("major pentatonic",                                  2,2,3,2,3)
  f("suspended pentatonic:egyptian",                     2,3,2,3,2)
  f("blues minor pentatonic:man gong",                   3,2,3,2,2)
  f("blues major pentatonic:ritusen",                    2,3,2,2,3)
  f("minor pentatonic",                                  3,2,2,3,2)

//****************************************************************************
}
//****************************************************************************
