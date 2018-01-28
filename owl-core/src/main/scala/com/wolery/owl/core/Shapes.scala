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
//*                                                                     0-0
//*                                                                   (| v |)
//**********************************************************************w*w***

package com.wolery
package owl
package core

import scala.collection.mutable.Map

/**
 * Provides additional information about scale shapes beyond that which can be
 * calculated from their underlying interval structure.
 *
 * Implements a little database of meta data for shapes as a pair of maps from
 * names (''byNames'') and shapes (''byShape'') to instances of class [[Info]].
 */
private[core]
object Shapes
{
  /**
   * Augments a scale shape with information that cannot be computed from its
   * interval structure alone, including:
   *
   *  - the preferred name of the shape
   *  - other names by which the shape is commonly known
   *
   * @param  shape  The shape for which we carry additional information.
   * @param  names  A ':' delimited list of names by which the shape is commonly
   *                known, beginning with its preferred name.
   */
  final class Info (val shape: Shape,_names: String)
  {
    /**
     * The preferred name of this shape.
     */
    def name: Name = _names.split(":",2).head

    /**
     * A list of names by which this shape is commonly known, beginning with
     * its preferred name.
     */
    def names: Seq[Name] = _names.split(":")
  }

  /**
   * Returns additional information about the scale shape with the given name.
   *
   * Not every shape ''has'' such additional information - there are thousands
   * of them, after all - so we return the result in the Option monad.
   *
   * @param  name  The name by which a scale shape is commonly known.
   */
  def info(name: Name): Option[Info] = byNames.get(normalize(name))

  /**
   * Returns additional information about the given scale shape.
   *
   * Not every shape ''has'' such additional information - there are thousands
   * of them, after all - so we return the result in the Option monad.
   *
   * @param  shape  A scale shape.
   */
  def info(shape: Shape): Option[Info] = byShape.get(shape)

  /**
   * Adds a scale shape to the database.
   *
   * The new shape is specified via a sequence of intervals (that is, integers
   * modulo 12) that is either:
   *
   *  - ''absolute'':   each interval is specified relative to the ultimate root
   *                    of the scale (e.g. `[0,2,4,5,7,9,11]` = diatonic)
   *
   *  - ''relative'':   each interval is specified relative to its predecessor
   *                    in the sequence (e.g. `[2,2,1,2,2,2,1]` = diatonic)
   *
   * @param  names      A ':' delimited list of names by which the scale shape
   *                    is commonly known, beginning with the preferred name.
   * @param  intervals  A sequence of intervals that specifies the underlying
   *                    interval structure of the new scale shape.
   */
  private def f(names: String,intervals: ℤ*) =
  {
    assert(names.nonEmpty)                               // At least one name

    val s = Shape(intervals: _*)                         // The new shape
    val i = new Info(s,names)                            // The new shape info

    def add[Key](map: Map[Key,Info],key: Key) =          // Add key → i to map
    {
      assert(!map.contains(key))                         // ...not yet added
      map += key → i                                     // ...add it now
      assert( map.contains(key))                         // ...now in there
    }

    for (name ← names.split(":"))                        // For each name
    {
      add(byNames,normalize(name))                       // Add to name index
    }

    add(byShape,i.shape)                                 // Add to shape index
  }

  /**
   * Reduce the given name to a ''normal form'' in which characters like -, +,
   * ♭, ♮, ♯, and # are replaced with Latin letters, yielding a string that is
   * suitable for use as a key in the ''byName'' dictionary.
   *
   * For example:
   * {{{
   *    normalize("DorIan ♭2")   =  "dorian b2"
   *    normalize("Locrian ♮2")  =  "locrian n2"
   *    normalize("Ionian ♯5")   =  "ionian s5"
   * }}}
   * @param   name  The name of a scale shape.
   *
   * @return A simplified form of the name suitable for use as a key in a map.
   */
  private def normalize(name: Name): Name = name.map
  {
    case  c if c.isUpper ⇒ c.toLower                     // To lower case
    case '♭' ⇒ 'b';case '-' ⇒ 'b';                       // Encode as 'b'
    case '♮' ⇒ 'n'                                       // Encode as 'n'
    case '♯' ⇒ 's';case '+' ⇒ 's';case '#' ⇒ 's';        // Encode as 's'
    case  c  ⇒  c                                        // Leave it alone
  }

  private val byNames: Map[Name, Info] = Map.empty       // Indexed by name
  private val byShape: Map[Shape,Info] = Map.empty       // Indexed by shape

//****************************************************************************
// Chromatic

  f("chromatic",                                         1,1,1,1,1,1,1,1,1,1,1,1)

// Symmetric Diminished

  f("whole half",                                        2,1,2,1,2,1,2,1)
  f("half whole",                                        1,2,1,2,1,2,1,2)

// Diatonic

  f("ionian:major:diatonic",                             2,2,1,2,2,2,1)
  f("dorian",                                            2,1,2,2,2,1,2)
  f("phrygian",                                          1,2,2,2,1,2,2)
  f("lydian",                                            2,2,2,1,2,2,1)
  f("myxolydian",                                        2,2,1,2,2,1,2)
  f("aeolian:minor:natural minor",                       2,1,2,2,1,2,2)
  f("locrian",                                           1,2,2,1,2,2,2)

// Melodic Minor

  f("melodic:melodic minor",                             2,1,2,2,2,2,1)
  f("dorian ♭2:phrygian ♮6)",                            1,2,2,2,2,1,2)
  f("lydian ♯5:lydian augmented:superlydian:acoustic",   2,2,2,2,1,2,1)
  f("lydian ♭7:lydian dominant:mixolydian ♯4,overtone",  2,2,2,1,2,1,2)
  f("mixolydian ♭6:melodic major",                       2,2,1,2,1,2,2)
  f("dorian ♭5:locrian ♮2:half diminished",              2,1,2,1,2,2,2)
  f("altered:altered dominant:superlocrian",             1,2,1,2,2,2,2)

// Harmonic Minor

  f("harmonic:harmonic minor",                           2,1,2,2,1,3,1)
  f("locrian ♯6",                                        1,2,2,1,3,1,2)
  f("ionian ♯5:ionian augmented",                        2,2,1,3,1,2,1)
  f("dorian ♯4:romanian",                                2,1,3,1,2,1,2)
  f("phrygian ♯3:phrygian dominant",                     1,3,1,2,1,2,2)
  f("lydian ♯2",                                         3,1,2,1,2,2,1)
  f("myxolydian ♯1:ultralocrian",                        1,2,1,2,2,1,3)

// Double Harmonic

  f("double harmonic:arabic:gypsy:byzantine",            1,3,1,2,1,3,1)
  f("lydian ♯2 ♯6",                                      3,1,2,1,3,1,1)
  f("ultraphrygian",                                     1,2,1,3,1,1,3)
  f("hungarian minor",                                   2,1,3,1,1,3,1)
  f("oriental",                                          1,3,1,1,3,1,2)
  f("ionian augmented ♯2",                               3,1,1,3,1,2,1)
  f("locrian 𝄫3 𝄫7",                                     1,1,3,1,2,1,3)

// Hexatonic

  f("whole tone",                                        2,2,2,2,2,2)
  f("prometheus",                                        2,2,2,3,1,2)
  f("augmented",                                         3,1,3,1,3,1)
  f("tritone",                                           1,2,3,1,3,2)
  f("blues",                                             3,2,1,1,3,2)

// Pentatonic

  f("major pentatonic",                                  2,2,3,2,3)
  f("suspended pentatonic:egyptian",                     2,3,2,3,2)
  f("blues minor pentatonic:man gong",                   3,2,3,2,2)
  f("blues major pentatonic:ritusen",                    2,3,2,2,3)
  f("minor pentatonic",                                  3,2,2,3,2)

//****************************************************************************
}
//****************************************************************************
