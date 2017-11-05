//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : A command line console control implemented as a TextArea.
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*
//*
//*  See Also: https://ss64.com/osx/syntax-bashkeyboard.html
//*                                                                     0-0
//*                                                                   (| v |)
//**********************************************************************w*w***

package com.wolery.owl
package control

import java.io.Writer
import java.lang.System.{lineSeparator ⇒ EOL}
import javafx.event.{ActionEvent,Event,EventHandler,EventType}
import javafx.scene.control.TextArea
import javafx.scene.input.{KeyCode,KeyEvent}
import javafx.scene.input.KeyEvent.{KEY_PRESSED,KEY_TYPED}
import scala.beans.BeanProperty
import scala.collection.mutable.Buffer

import util._

/**
 * A command line console control implemented as a JavaFX TextArea control.
 *
 * = Overview =
 *
 * The Console control partitions the contents of its inherited text area into
 * three disjoint areas:
 *
 *  - Output Area  A read-only area of text that extends from the beginning of
 *                 the underlying TextArea up to the prompt. It is intended to
 *                 record the output of whatever commands have previously been
 *                 accepted, and is modified by the client either by using the
 *                 appendText() API,  or by appending to the public ''writer''
 *                 property.
 *
 *  - Prompt       A read-only area of text that  extends from just beyond the
 *                 output area up to the input area. It is intended to display
 *                 some sort of short prompt string, and is assigned to by the
 *                 client either by appending to the output area as above,  or
 *                 else by assigning to the public ''prompt'' property.
 *
 *  - Input Area   An editable area of text  that lies  immediately beyond the
 *                 prompt. The user interacts with the characters in this area
 *                 directly by entering keystrokes in the usual way, until the
 *                 input is finally accepted by typing the ENTER key, at which
 *                 point an ActionEvent is fired. The input area is exposed to
 *                 the client through the mutable ''input'' property.
 *
 * The client - usually a Controller of some sort -  registers interest in the
 * acceptance of an input line by assigning a handler to the ''onAccept'' bean
 * property.
 *
 * The client would typically respond to the event by processing the new input
 * string in some way, appending text to the output area, recording the string
 * in the command history, and finally by updating the prompt string, ready to
 * edit another line of input.
 *
 * An ActionEvent is also fired when the TAB key is pressed. This ''Complete''
 * event gives the controller an opportunity  to prompt the user with possible
 * completions of the current partially edited command line.
 *
 * = History =
 *
 * The control maintains an array of strings known as the ''command history''.
 * Shell-style key bindings manipulate a cursor into this array, and enable an
 * element to be quickly summoned into the input area for further editing. The
 * history can also be incrementally searched for strings that match a certain
 * character pattern.
 *
 * The maximum number strings held in the command history can be specified via
 * the 'History Size' bean property.
 *
 * = Bean Properties =
 *
 * The control exposes a number of bean properties that can be specified from
 * within the Scene Builder application:
 *
 *  - History Size  The maximum number of elements in the command history.
 *  - onAccept      The event handler for the ''Accept''   ActionEvent.
 *  - onComplete    The event handler for the ''Complete'' ActionEvent.
 *
 * = Scala Properties =
 *
 * The control also exposes a number of ordinary Scala properties:
 *
 *  - output        The current contents of the output area (read only).
 *  - prompt        The current contents of the prompt area.
 *  - input         The current contents of the input area.
 *  - writer        A java.io.Writer that appends text to the output area.
 *
 * = Internals =
 *
 * The control maintains the following internal state variables:
 *
 *  - m_prompt      The start of prompt area.
 *  - m_input       The start of the input area.
 *  - m_toggle      The position of the caret  within the input area prior to
 *                  toggling back to the start of the input area.
 *  - m_period      The position of the caret  within the input area prior to
 *                  summoning the final word of the previous command into the
 *                  input area.
 *  - m_cursor      The index into the command history of the current command.
 *  - m_latest      The index into the command history of the most recent
 *                  string to have been added.
 *  - m_command     A cyclic array of previously accepted command strings.
 *  - m_filters     The event filters that define the current key bindings and 
 *                  so the effective behavior of the control.
 *
 * @see    [[https://ss64.com/osx/syntax-bashkeyboard.html Bash Keyboard Shortcuts]]
 * @author Jonathon Bell
 */
class Console extends TextArea with Logging
{
  @BeanProperty var onAccept  : EventHandler[ActionEvent] = _
  @BeanProperty var onComplete: EventHandler[ActionEvent] = _

  /**
   * TODO
   */
  def getHistorySize: ℕ =
  {
    m_command.size
  }

  /**
   * TODO
   *
   * @param
   */
  def setHistorySize(size: ℕ): Unit =
  {
    log.debug("setHistorySize({})",size)

    val min: ℕ = 0
    val max: ℕ = 10
    val δ  : ℤ = size - m_command.size

    if (size<min || max<size)
    {
      log.warn(s"Bad history size $size: should be $min<=size<=$max")

      setHistorySize(clamp(min,size,max))
    }
    else
    if (δ > 0)
    {
      m_command ++= Seq.fill(δ)("")
    }
    else
    if (δ < 0)
    {
      m_command.trimEnd(-δ)
    }

    assert(getHistorySize == size)
  }

//****************************************************************************
  private type Filters = Seq[(EventType[KeyEvent],EventHandler[KeyEvent])]

  private var m_prompt : ℕ = 0                           // Start of prompt
  private var m_input  : ℕ = 0                           // Start of input
  private var m_toggle : ℕ = 0                           // Saved caret point
  private var m_period : Option[ℕ] = None                           // Saved caret point

  private var m_cursor : ℕ = 0                           // The history cursor
  private var m_latest : ℕ = 0                           //
  private val m_command: Buffer[String] = Buffer.fill(10)("")

  private val m_filters: Filters = Seq((KEY_PRESSED,onKeyPressed _),
                                       (KEY_TYPED,  onKeyTyped   _))

  swap(Seq(),m_filters)

//****************************************************************************

  def output: String =
  {
    getText(0,m_prompt)
  }

  def prompt: String =
  {
    getText(m_prompt,m_input)
  }

  def prompt_=(string: String): Unit =
  {
    super.replaceText(m_prompt,m_input,string)

    setInputArea(m_prompt + string.length)

    assert(isConsistent)                                 // Check consistency
  }

  def input: String =
  {
    getText(m_input,getLength)
  }

  def input_=(string: String): Unit =
  {
    super.replaceText(m_input,getLength,string)

    assert(isConsistent)                                 // Check consistency
  }

  val writer: Writer = new Writer
  {
    def close: Unit = {}

    def flush: Unit = {}

    def write(array: Array[Char],offset: ℕ,length: ℕ): Unit =
    {
      appendText(new String(array.slice(offset,offset + length)))
    }
  }

  override
  def replaceText(start: ℕ,end: ℕ,string: String) =
  {
    log.trace("replaceText({})",s"$start,$end,$string")

    if (start >= m_input)
    {
      super.replaceText(start,end,string)
    }

    assert(isConsistent)                                 // Check consistency
  }

  override
  def appendText(string: String) =
  {
    log.trace("appendText({})",string)

    super.appendText(string)

    m_prompt = getLength

    setInputArea(getLength)

    assert(isConsistent)                                 // Check consistency
  }

  def appendLine(string: String) =
  {
    log.debug("appendLine({})",string)

    appendText(string + EOL)

    assert(isConsistent)                                 // Check consistency
  }

  override
  def selectRange(anchor: ℕ,caret: ℕ): Unit =
  {
    log.trace("selectRange({},{})",anchor,caret)

    val n = getLength
    val a = clamp(m_input,anchor,n)
    val c = clamp(m_input,caret, n)

    super.selectRange(a,c)

    assert(isConsistent)                                 // Check consistency
  }

  /**
   * TODO
   */
  def accept(): Unit =
  {
    log.info("accept({})",input)

    super.appendText(EOL)

    if (onAccept != null)
    {
      onAccept.handle(new ActionEvent)
    }

    setInputArea(getLength)
  }

  /**
   * TODO
   */
  def complete(): Unit =
  {
    log.info("complete({})",input)

    if (onComplete != null)
    {
      onComplete.handle(new ActionEvent)
    }
  }

  /**
   * TODO
   */
  override
  def home(): Unit =
  {
    log.debug("home()")

    m_toggle = m_input

    positionCaret(m_input)
  }

  /**
   * TODO
   */
  override
  def forward(): Unit =
  {
    log.debug("forward()")

    when (getCaretPosition < getLength)
    {
      super.forward()
    }

    positionCaret(getCaretPosition) // clear selection
  }

  /**
   * TODO
   */
  override
  def backward(): Unit =
  {
    log.debug("backward()")

    when (m_input < getCaretPosition)
    {
      super.backward()
    }

    positionCaret(getCaretPosition) // clear selection
  }

  /**
   * TODO
   */
  def toggleHome(): Unit =
  {
    log.debug("toggleHome({})",m_toggle)

    val c = getCaretPosition

    if (m_input < c)
    {
      m_toggle = c
      positionCaret(m_input)
    }
    else
    {
      positionCaret(m_toggle)
    }
  }

  /**
   * TODO
   */
  override
  def deletePreviousChar(): Bool =
  {
    log.debug("deletePreviousChar()")

    when (super.deletePreviousChar())
    {}
  }

  /**
   * TODO
   */
  override
  def deleteNextChar(): Bool =
  {
    log.debug("deleteNextChar()")

    when (super.deleteNextChar())
    {}
  }

  /**
   * TODO
   */
  def deletePreviousWord(): Unit =
  {
    log.debug("deletePreviousWord()")

    val c = getCaretPosition
    previousWord()
    deleteText(getCaretPosition,c)
  }

  /**
   * TODO
   */
  def deleteNextWord(): Unit =
  {
    log.debug("deleteNextWord()")

    val c = getCaretPosition
    endOfNextWord()
    deleteText(c,getCaretPosition)
  }

  /**
   * TODO
   */
  def cutPreviousWord(): Unit =
  {
    log.debug("cutPreviousWord()")

    when (m_input > getCaretPosition)
    {
      selectPreviousWord()
      cut()
    }
  }

  /**
   * TODO
   */
  def cutToEnd(): Unit =
  {
    log.debug("cutToEnd()")

    selectRange(getCaretPosition,getLength)
    cut()
  }

  /**
   * TODO
   */
  def cutToHome(): Unit =
  {
    log.debug("cutToHome()")

    selectRange(m_input,getCaretPosition)
    cut()
  }

  /**
   * TODO
   */
  def swapChars(): Unit =
  {
    log.debug("swapChars()")

    var c = getCaretPosition
    val e = getLength

    when (m_input + 2 <= c)
    {
      if (c < e)
      {
        c -= 1
      }
      else
      {
        c -= 2
      }

      val s = getText(c,c+2)
      val t = s"${s(1)}${s(0)}"

      replaceText(c,c+2,t)
    }
  }

  /**
   * TODO
   */
  def xformChar(xform: String ⇒ String): Unit =
  {
    log.debug("transformChar()")

    var c = getCaretPosition

    if (c < getLength)
    {
      endOfNextWord()
      previousWord()
      c = getCaretPosition
      replaceText(c,c+1,xform(getText(c,c+1)))
      endOfNextWord()
    }
  }

  /**
   * TODO
   */
  def xformWord(xform: String ⇒ String): Unit =
  {
    log.debug("transformWord()")

    val c = getCaretPosition

    if (c < getLength)
    {
      endOfNextWord()
      val e = getCaretPosition
      replaceText(c,e,xform(getText(c,e)))
    }
  }

  /**
   * TODO
   */
  def addCommand(command: String): Unit =
  {
    log.debug("addCommand({})",command)

    m_command(m_latest % m_command.size) = command
    m_latest += 1
    m_cursor  = m_latest
  }

  /**
   * TODO
   */
  def getCommand(index: ℕ): String =
  {
    log.debug("getCommand({})",index)

    m_command(index % m_command.size)
  }

  /**
   * TODO
   */
  def cursor(δ: ℤ): Unit =
  {
    log.debug("cursor({})",δ)                            // Trace our location

    when (commandRange.inclusive.contains(m_cursor + δ)) // Is still in range?
    {
      m_cursor += δ                                      // ...adjust cursor
    }

    input = if (commandRange.contains(m_cursor)) getCommand(m_cursor) else ""
  }

  /**
   * Formats the current contents of the command history and appends it to the
   * given writer.
   *
   * @param  count   The number of commands to include in the listing.
   * @param  writer  The writer to which we will append the formatted output.
   */
  def listCommands(count: ℕ = m_command.size,writer: Writer = this.writer): Unit =
  {
    log.debug("listCommands()")                          // Trace our location

    for (i ← commandRange.takeRight(count))              // For each command
    {
      writer.append(f"${i+1}%5d  ${getCommand(i)}%s\n")  // ...format command
    }
  }

  /**
   * TODO Restore the final argument of the previous
   */
  def lastWord(δ: ℤ): Unit =
  {
    log.debug("lastArgument({})",m_cursor)

    when (commandRange.inclusive.contains(m_cursor + δ)) // Is still in range?
    {
      m_cursor += δ                                      // ...adjust cursor
    }

    val c = getCaretPosition

    if (m_period.isEmpty)
    {
      m_period = Some(c)
    }

    replaceText(m_period.get,c,m_command(m_cursor).split(" ").last)
  }

  /**
   * TODO
   */
  def findCommand(): Unit =
  {
    log.debug("findCommand()")

    new Search(prompt,input,m_filters)
  }

  /**
   * Implements an incremental reverse search of the command history.
   *
   * Constructed from a snapshot of the enclosing parent's mutable state, this
   * object temporarily re-maps the keyboard by swapping the KeyEvent filters.
   * This places the Console into a mode in which characters typed by the user
   * refine a  search pattern that filters the command history and updates the
   * input area interactively.
   *
   * Canceling the search restores the event filters, prompt, and input areas,
   * and may also fire an Accept event.
   *
   * @param  old_prompt   The current contents of the prompt area.
   * @param  old_input    The current contents of the input area.
   * @param  old_filters  The current KeyEvent filters.
   */
  private
  class Search (old_prompt: String,old_input: String,old_filters: Filters)
  {
    var m_cursor : ℕ           = 0                       // Current position
    var m_pattern: String      = old_input               // Current pattern
    var m_matches: Seq[String] = Seq()                   // Current matches
    val m_filters: Filters     = Seq((KEY_PRESSED,onKeyPressed _ ),
                                     (KEY_TYPED,  onKeyTyped   _ ))

    swap(old_filters,m_filters)                          // Enter search mode

    refine()                                             // Refine the search

    /**
     * Cancels the search by restoring the KeyEvent filters, prompt, and input
     * areas, and optionally placing the given string back on the command line
     * for subsequent editing.
     *
     * @param  string  The new contents of the input area.
     */
    def cancel(string: String = old_prompt): Unit =
    {
      log.debug("search.cancel({})",string)              // Trace our location

      prompt = old_prompt                                // Restore the prompt
      input  = string                                    // Restore the input
      swap(m_filters,old_filters)                        // Cancel search mode

      assert(isConsistent)                               // Check consistency
    }

    /**
     * Updates the prompt area with the current search pattern, and input area
     * with the given string.
     *
     * @param  string  The new contents of the input area.
     */
    def update(string: String = "") : Unit =
    {
      log.debug("search.update({})",string)              // Trace our location

      prompt = s"(reverse-i-search)'$m_pattern': "       // Update prompt area
      input  = string                                    // Update input area
    }

    /**
     * Updates the prompt area with the current search pattern, and input area
     * with the given string.
     *
     * @param  string  The new contents of the input area.
     */
    def backspace(): Unit =
    {
      log.debug("search.backspace()")                    // Trace our location

      m_pattern = m_pattern.dropRight(1)                 // Drop one character

      refine()                                           // Refine the search
    }

    /**
     * Adjust the command history cursor by the given delta.
     *
     * @param  δ  The number of slots to displace the cursor by.
     */
    def cursor(δ: ℤ): Unit =
    {
      log.debug("search.moveCursor({})",δ)               // Trace our location

      when (m_matches.isDefinedAt(m_cursor + δ))         // Is still in range?
      {
        m_cursor += δ                                    // ...displace cursor
        val m = m_matches(m_cursor)                      // ...get the command
        update(m)                                        // ...set the input
        positionCaret(m_input+m.indexOfSlice(m_pattern)) // ...point at match
      }
    }

    /**
     * Refines the current search to incorporate the given characters into the
     * search pattern.
     *
     * @param  chars  The characters to append to the current search pattern.
     */
    def refine(chars: String = ""): Unit =
    {
      log.debug("search.refine({})",chars)               // Trace our location

      m_pattern+= chars                                  // Update the pattern

      m_matches = m_command.filter(_.contains(m_pattern))// Filter the history

      if (m_matches.nonEmpty)                            // Matching commands?
      {
        m_cursor = m_matches.size - 1                    // ...the final match

        cursor(0)                                        // ...position cursor
      }
      else                                               // No commands found
      {
        update()                                         // ...clear the input
      }
    }

    /**
     * Processes the current keystroke.
     *
     * @param  e  The current keystroke.
     */
    def onKeyPressed(e: KeyEvent): Unit =
    {
      log.debug("onKeyPressed{} [{}]",getKeyCombo(e),e.getCharacter,"")

      import KeyCode._                                   // For key code names

      getKeyCombo(e) match                               // Which combination?
      {
        case ('_,BACK_SPACE) ⇒ backspace()               // ...back up a char
        case ('^,G)          ⇒ cancel()                  // ...cancel search
        case ('^,R)          ⇒ cursor(-1)                // ...previous match
        case ('^,S)          ⇒ cursor(+1)                // ...next match
        case ('_,ENTER)      ⇒ cancel(input);accept()    // ...accept match
        case (_,TAB |ESCAPE
               |UP  |DOWN
               |LEFT|RIGHT)  ⇒ cancel(input)             // ...cancel search
        case  _              ⇒                           // ...discard event
      }

      e.consume()                                        // We have handled it
    }

    /**
     * Processes the current keystroke.
     *
     * @param  e  The current keystroke.
     */
    def onKeyTyped(e: KeyEvent): Unit =
    {
      log.debug("onKeyTyped  {} [{}]",getKeyCombo(e),e.getCharacter,"")

      val c = e.getCharacter                             // Character entered

      if (c.nonEmpty && isSearchable(c(0)))              // Can search on it?
      {
        refine(c)                                        // ...add to pattern
      }

      e.consume()                                        // We have handled it
    }
  } /************************************************************************/

  /**
   * TODO
   */
  private
  def onKeyTyped(e: KeyEvent): Unit =
  {
    log.debug("onKeyTyped  {} [{}]",getKeyCombo(e),e.getCharacter,"")

    if (e.isAltDown)
    {
      e.consume()
    }
  }

  /**
   * TODO
   */
  private
  def onKeyPressed(e: KeyEvent): Unit =
  {
    log.debug("onKeyPressed{} [{}]",getKeyCombo(e),e.getCharacter,"")

    import KeyCode._                                     // For key code names

    var period   = true
    var consumed = true

    getKeyCombo(e) match
    {
   // Cursor Movement:

      case ('^,A|LEFT)      ⇒ home()
      case ('^,E|RIGHT)     ⇒ end()
      case ('^,F)           ⇒ forward()
      case ('^,B)           ⇒ backward()
      case ('⌥,F)           ⇒ endOfNextWord()
      case ('⌥,B)           ⇒ previousWord()
      case ('^,X)           ⇒ toggleHome()

   // Selection

      case ('⇧^,A|LEFT)     ⇒ selectHome()
      case ('⇧^,E|RIGHT)    ⇒ selectEnd()

   // Input Editing:

      case ('⌥,BACK_SPACE)  ⇒ deletePreviousWord()
      case ('⌥,D)           ⇒ deleteNextWord()
      case ('^,D)           ⇒ deleteNextChar()
      case ('^,H)           ⇒ deletePreviousChar()
      case ('^,W)           ⇒ cutPreviousWord()
      case ('^,K)           ⇒ cutToEnd()
      case ('^,U)           ⇒ cutToHome()
      case ('^,T)           ⇒ swapChars()
      case ('^,Y)           ⇒ paste()
      case ('⌥,U)           ⇒ xformWord(_.toUpperCase)
      case ('⌥,L)           ⇒ xformWord(_.toLowerCase)
      case ('⌥,C)           ⇒ xformChar(_.toUpperCase)

   // Command History:

      case ('⌥,R)           ⇒ cursor(0)
      case ('^,P)|('_,UP)   ⇒ cursor(-1)
      case ('^,N)|('_,DOWN) ⇒ cursor(+1)
      case ('^,R)           ⇒ findCommand()
      case ('⌥,PERIOD)      ⇒ lastWord(-1);period = false
      case ('⌥,SLASH)       ⇒ lastWord(+1);period = false

   // Events:

      case ('_,ENTER)       ⇒ accept()
      case ('_,TAB)         ⇒ complete()

   // Disabled:

      case ('◆ | '⇧◆,Z)     ⇒

   // Anything Else...

      case  _               ⇒ consumed = false
    }

    if (period)
    {
      m_period = None
    }

    if (consumed)
    {
      e.consume()
    }
  }

 /**
   * Returns true if the given character is one we consider 'searchable'. This
   * currently includes the ASCII printing characters,  but could be broadened
   * to include other Unicode symbols in the future.
   *
   * @param  character  A character to consider for inclusion in the current
   *                    search pattern.
   *
   * @return `true` if the given character is on that can be searched for.
   */
  @inline private
  def isSearchable(character: Char): Bool =
  {
    isBetween(character,0x20,0x7E)                       // Is it printable?
  }

  /**
   * Defers the given side-effecting computation to run in the near future on
   * a background thread, after the calling function has returned.
   *
   * @param  action  A side-effecting action to perform in the near future,
   */
  @inline private
  def defer[α](action: ⇒ α): Unit =
  {
    javafx.application.Platform.runLater(() ⇒ action)    // Convert to lambda
  }

  /**
   * TODO
   *
   * @param  input
   */
  private
  def setInputArea(input: ℕ): Unit =
  {
    log.trace("setInputArea({},{})",m_prompt,input)      // Trace our location

    m_input  = input                                     // Set up input area
    m_toggle = input                                     // Clear caret toggle
    m_period = None                                      // Clear insert point
    m_cursor = m_latest

    assert(isConsistent)                                 // Check consistency
  }

  /**
   * TODO
   */
  private
  def commandRange: Range =
  {
    if (getCommand(m_latest).isEmpty)
      0 until m_latest
    else
      m_latest - m_command.size until m_latest
  }

  /**
   * TODO
   */
  private
  def getKeyCombo(e: KeyEvent): (Symbol,KeyCode) =
  {//⇧^⌥◆
    val                   c  = e.getCode
    var                   s  = ""
    if (e.isShiftDown)    s += '⇧'
    if (e.isControlDown)  s += '^'
    if (e.isAltDown)      s += '⌥'
    if (e.isMetaDown)     s += '◆'

    if (s.isEmpty()) ('_,c) else (Symbol(s),c)
  }

  /**
   * TODO
   */
  private
  def when(condition: Bool)(action: ⇒ Unit): Bool =
  {
    if (condition)
    {
      action
    }
    else
    {
      beep()
    }

    condition
  }

  /**
   * Swap the KeyEvent
   *
   * @param  was  The current KeyEvent filters handling our key events.
   * @param  now  The new KeyEvent filters that will handle future key events.
   */
  private
  def swap(was: Filters,now: Filters): Unit = defer
  {
    was.foreach{case (t,f) ⇒ removeEventFilter(t,f)}     // Remove old filters
    now.foreach{case (t,f) ⇒ addEventFilter   (t,f)}     // Append new filters
  }

  /**
   * Returns true if the object appears to be in a consistent state.
   *
   * Centralizes a number of consistency checks that otherwise tend to clutter
   * up the code.  Since only ever called from within assertions, these can be
   * eliminated from the release build by the compiler entirely.
   *
   * @return true - always.
   */
  private
  def isConsistent: Bool =
  {
    assert(isIncreasing(0,m_prompt,m_input,m_toggle,getLength))
    true
  }
}

//****************************************************************************
