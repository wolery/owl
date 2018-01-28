//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : An idiomatic Scala interface to the core Java preferences API.
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*                                                                     0-0
//*                                                                   (| v |)
//**********************************************************************w*w***

package com.wolery
package owl
package util

import java.util.prefs.{Preferences ⇒ JavaPreferences}

/**
 * Represents a single named, mutable, persistent, value - a ''preference'' -
 * of type `α`.
 *
 * Preferences are persisted transparently in a platform specific manner. Each
 * has a sensible default value and can be reverted back to the default at any
 * time.
 *
 * The class supports a number of idiomatic styles for accessing its value:
 * {{{
 *    val p: Preference[Boolean] = preferences.bool("enabled",false)
 *
 *    if (p.value)                                        // Get preference
 *      p.value =  ...                                    // Set preference
 *    if (p())                                            // Get preference
 *      p(false)   ...                                    // Set preference
 *    else
 *      p() =      ...                                    // Set preference
 * }}}
 *
 * @tparam α  The type of value being managed by the preference.
 *
 * @see    [[Preferences]] for a factory that creates the preferences.
 * @see    [[http://apps.tempel.org/PrefsEditor Mac OS X Prefs Editor]] for a
 *         utility that edits and manipulates preference settings on OS X.
 * @see    [[https://docs.oracle.com/javase/8/docs/technotes/guides/preferences/index.html
 *         Core Java Preferences API]] for details of the underlying API this
 *         interface wraps.
 *
 * @author Jonathon Bell
 */
trait Preference[α]
{
  /**
   * The name of the preference.
   */
  val name: String

  /**
   * Returns the current value of the preference.
   */
  def value: α

  /**
   * Updates the current value of the preference with the given value.
   *
   * @param  value  The new value for the preference.
   */
  def value_=(value: α): Unit

  /**
   * Reverts the preference to its original default value.
   */
  def revert(): Unit

  /**
   * Returns the current value of the preference.
   */
  @inline final
  def apply(): α = value

  /**
   * Updates the current value of the preference with the given value.
   *
   * @param  value  The new value for the preference.
   */
  @inline final
  def apply (value: α): Unit = value_=(value)

  /**
   * Updates the current value of the preference with the given value.
   *
   * @param  value  The new value for the preference.
   */
  @inline final
  def update(value: α): Unit = value_=(value)
}

/**
 * Implements a factory for creating preferences of various primitive types.
 *
 * The class is designed to be extended by an application specific class that
 * binds names to each of the preferences it wishes to track.
 *
 * For example:
 * {{{
 *    object prefs extends Preferences("mypath")
 *    {
 *      val enabled: Preference[Bool] = bool("enabled","true")
 *          ...                                    // ...and other preferences
 *    }
 *
 *    if (prefs.enabled())                         // Get preference 'enabled'
 *        prefs.enabled() = false                  // Set preference 'enabled'
 *
 * }}}
 *
 * @see    [[Preference]] for the interface to the preferences themselves.
 * @see    [[http://apps.tempel.org/PrefsEditor Mac OS X Prefs Editor]] for a
 *         utility that edits and manipulates preference settings on OS X.
 * @see    [[https://docs.oracle.com/javase/8/docs/technotes/guides/preferences/index.html
 *         Core Java Preferences API]] for details of the underlying API that
 *         this interface wraps.
 *
 * @author Jonathon Bell
 */
class Preferences private (private val m_imp: JavaPreferences)
{
  /**
   * Creates a preference factory whose preferences are persisted to the given
   * location within the user preference tree.
   *
   * The location is specified as a `/` delimited path,  given relative to the
   * the root of the user preference tree.
   *
   * Unless the location existed prior to construction, the subtree may not be
   * committed to disk until once the `flush` method has been called.
   *
   * @param  path  The path to the persisted values, specified relative to the
   *               root of user preference tree.
   *
   * @see    `flush()`
   */
  def this(path: String) =
  {
    this(JavaPreferences.userRoot.node(path))
  }

  /**
   * Creates a preference factory whose preferences are persisted to the given
   * location within the user preference tree.
   *
   * The location is specified as a `/` delimited path, given relative to the
   * the root of the user preference tree, and is generated automatically from
   * the package name of the given class  by replacing `.` characters with `/`
   * characters.
   *
   * Unless the location existed prior to construction, the subtree may not be
   * committed to disk until once the `flush` method has been called.
   *
   * @param  c  The class whose package name specifies the path to the values,
   *            specified relative to the root of user preference tree.
   *
   * @see    `flush()`
   */
  def this(klass: Class[_]) =
  {
    this(JavaPreferences.userNodeForPackage(klass))
  }

  /**
   * Creates a preference factory whose preferences are persisted to the given
   * location within the user preference tree.
   *
   * The location is specified as a `/` delimited path, given relative to the
   * the root of the user preference tree.
   *
   * Unless the location existed prior to construction, the subtree may not be
   * committed to disk until once the `flush` method has been called.
   *
   * @param  parent  An existing parent preferences factory.
   * @param  path    The path to the preference values, specficied relative to
   *                 the root of the given parent's preferences.
   *
   * @see    `flush()`
   */
  def this(parent: Preferences,path: String) =
  {
    this(parent.m_imp.node(path.dropWhile(_ == '/')))    // Strip leading '/'s
  }

  /**
   * Returns the named preference node in the same tree as this node, creating
   * it and any of its ancestors if they do not already exist.
   *
   * The location is specified as a `/` delimited path,  given relative to the
   * the root of the user preference tree.
   *
   * Unless the location existed prior to construction, the subtree may not be
   * committed to disk until once the `flush` method has been called.
   *
   * @param  path  The path to the persisted values, specified relative to the
   *               root of user preference tree if absolute (starts with `/`),
   *               or this object's preferences if relative (starts with a non
   *               `/` character).
   *
   * @return The specified preference factory.
   * @see    `flush()`
   */
  def node(path: String): Preferences =
  {
    new Preferences(m_imp.node(path))
  }

 /**
   * Commits any changes that may have been made to our preferences  (or those
   * of our descendants) to disk.
   *
   * @see    `sync()`
   */
  def flush(): Unit = m_imp.flush()

  /**
   * Ensures that future reads from this preference  node and its  descendants
   * reflect any changes that were committed to the persistent store (from any
   * VM) prior to the `sync()` invocation. As a side-effect, forces changes in
   * the contents of this node and its descendants to the persistent store, as
   * if the `flush()` method had been invoked on this node.
   *
   * @see    `flush()`
   */
  def sync(): Unit = m_imp.sync()

  /**
   * Describes the type of a factory method that constructs a `Preference [α]`
   * from the name and default value of the preference.
   *
   * @tparam α  The type of value being managed by the generated preference.
   */
  type Atomic[α] = (Name,α) ⇒ Preference[α]

  /**
   * Describes the type of a factory method that constructs a `Preference [Seq[α]]`
   * from the name and default value of the preference.
   * @tparam α  The element type of the sequence being managed by the generated
   *             preference.
   */
  type Sequence[α] = (Name,Seq[α]) ⇒ Preference[Seq[α]]

  /**
   * Creates a `Preference[Int]` with the given name and default value.
   *
   * @param name     The name of the new preference.
   * @param default  The default value for the new preference.
   *
   * @return The new preference.
   */
  val int: Atomic[Int] = atomic(_.toInt)

  /**
   * Creates a `Preference[Bool]` with the given name and default value.
   *
   * @param name     The name of the new preference.
   * @param default  The default value for the new preference.
   *
   * @return The new preference.
   */
  val bool: Atomic[Bool] = atomic(_.toBoolean)

  /**
   * Creates a `Preference[Long]` with the given name and default value.
   *
   * @param name     The name of the new preference.
   * @param default  The default value for the new preference.
   *
   * @return The new preference.
   */
  val long: Atomic[Long] = atomic(_.toLong)

  /**
   * Creates a `Preference[Float]` with the given name and default value.
   *
   * @param name     The name of the new preference.
   * @param default  The default value for the new preference.
   *
   * @return The new preference.
   */
  val float: Atomic[Float] = atomic(_.toFloat)

  /**
   * Creates a `Preference[Double]` with the given name and default value.
   *
   * @param name     The name of the new preference.
   * @param default  The default value for the new preference.
   *
   * @return The new preference.
   */
  val double: Atomic[Double] = atomic(_.toDouble)

  /**
   * Creates a `Preference[String]` with the given name and default value.
   *
   * @param name     The name of the new preference.
   * @param default  The default value for the new preference.
   *
   * @return The new preference.
   */
  val string: Atomic[String] = atomic(_.toString)

  /**
   * Creates a `Preference[Seq[Int]]` with the given name and default value.
   *
   * @param name     The name of the new preference.
   * @param default  The default value for the new preference.
   *
   * @return The new preference.
   */
  val ints: Sequence[Int] = sequence(_.toInt)

  /**
   * Creates a `Preference[Seq[Bool]]` with the given name and default value.
   *
   * @param name     The name of the new preference.
   * @param default  The default value for the new preference.
   *
   * @return The new preference.
   */
  val bools: Sequence[Bool] = sequence(_.toBoolean)

  /**
   * Creates a `Preference[Seq[Long]]` with the given name and default value.
   *
   * @param name     The name of the new preference.
   * @param default  The default value for the new preference.
   *
   * @return The new preference.
   */
  val longs: Sequence[Long] = sequence(_.toLong)

  /**
   * Creates a `Preference[Seq[Float]]` with the given name and default value.
   *
   * @param name     The name of the new preference.
   * @param default  The default value for the new preference.
   *
   * @return The new preference.
   */
  val floats: Sequence[Float] = sequence(_.toFloat)

  /**
   * Creates a `Preference[Seq[Double]]` with the given name and default value.
   *
   * @param name     The name of the new preference.
   * @param default  The default value for the new preference.
   *
   * @return The new preference.
   */
  val doubles: Sequence[Double] = sequence(_.toDouble)

  /**
   * Creates a `Preference[Seq[String]]` with the given name and default value.
   *
   * @param name     The name of the new preference.
   * @param default  The default value for the new preference.
   *
   * @return The new preference.
   */
  val strings: Sequence[String] = sequence(_.toString)

  /**
   * Creates a factory function for preferences of type `α` that use the given
   * parser and printer functions to serialize values to and from a string.
   *
   * The functions should be mutual inverses of one anther. That is:
   * {{{
   *    parse ∘ print  ==  identity[α]
   * }}}
   * where `∘` denotes function composition, and `identity[α]` is the identity
   * function for values of type `α`.
   *
   * @tparam α        The type of value being managed by the preference.
   * @param  parse    Parses the preference value from a string.
   * @param  print    Formats the preference value as a string.
   * @param  key      The name of the preference.
   * @param  default  The default value for the preference.
   *
   * @return A function that constructs a `Preference[α]` from a name and
   *         default value pair of type `(Name,α)`.
   */
  private
  def atomic[α](parse: String ⇒ α,print: α ⇒ String = (a: α) ⇒ a.toString)
               (key: Name,default: α) : Preference[α] = new Preference[α]
  {
    val name          = key                              // The preference key
    val dflt          = print(default)                   // The default value
    def value         = parse(m_imp.get(key,dflt))       // Get the preference
    def value_=(v: α) = m_imp.put(key,print(v))          // Set the preference
    def revert()      = m_imp.remove(name)               // Remove saved value
    override
    def toString      = s"{$name = $value [$default]}"   // Format as a string
  }

  /**
   * Creates a  factory function for  preferences of type `Preference[Seq[α]]`
   * that use the given parser to retrieve each element from a string.
   *
   * The resulting preference  persists a sequence by serializing each element
   * as a string and concatenating these with a  delimiting `◇` character, and
   * delegates to the given parser to read the elements back again.
   *
   * @tparam α      The type of a sequence element.
   * @param  parse  A parser for elements of type `α`.
   *
   * @return A function that constructs a `Preference[Seq[α]]` from a name and
   *         default value pair of type `(Name,Seq[α])`.
   */
  private
  def sequence[α](parse: String ⇒ α): Sequence[α] =
  {
    atomic(_.split("◇").map(parse),_.mkString("◇"))      // The factory method
  }
}

//****************************************************************************
