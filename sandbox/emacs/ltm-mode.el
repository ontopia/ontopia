
;;;; A major mode for editing LTM (Linear Topic Map notation).
;;;; Adds font locking support.

(defvar ltmm-version "0.02"
  "The current version number of ltm-mode.")

;;; copyright (c) 2003 Ontopia AS, <larsga@ontopia.net>
;;; $Id: ltm-mode.el,v 1.7 2008/12/04 12:46:39 lars.garshol Exp $

;;; ltm-mode is free software; you can redistribute it and/or modify
;;; it under the terms of the GNU General Public License as published
;;; by the Free Software Foundation; either version 2, or (at your
;;; option) any later version.
;;;
;;; ltm-mode is distributed in the hope that it will be useful, but
;;; WITHOUT ANY WARRANTY; without even the implied warranty of
;;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
;;; General Public License for more details.
;;;
;;; You should have received a copy of the GNU General Public License
;;; along with GNU Emacs; see the file COPYING.  If not, write to the
;;; Free Software Foundation, 675 Mass Ave, Cambridge, MA 02139, USA.

; Send me an email if you want new features (or if you add them
; yourself).  I will do my best to preserve the API to functions not
; explicitly marked as internal and variables shown as customizable. I
; make no promises about the rest.

; Bug reports are very welcome. 

; To install, put this in your .emacs:
;
; (autoload 'ltm-mode "ltm-mode")
; (setq auto-mode-alist       
;      (cons '("\\.ltm\\'" . ltm-mode) auto-mode-alist))

;; Required modules
(require 'font-lock)

;;; XEmacs compatibility

(when (not (boundp 'font-lock-constant-face))
  (defvar font-lock-constant-face font-lock-reference-face))
  
;;; The code itself

(defvar ltm-font-lock-keywords
  (list
   (cons "\\[\\[[^]]*\\]\\]" font-lock-constant-face)
   (cons "#[A-Z]+" font-lock-reference-face)
   ;(cons ":\\([ \t\r\n]+[A-Za-z_][-A-Za-z_0-9.]+\\)+" font-lock-builtin-face)
   (cons "/\\([ \t\r\n]*[A-Za-z_][-A-Za-z_0-9.]+\\)+" font-lock-keyword-face)
   (cons "{[A-Za-z_][-A-Za-z_0-9.]+" font-lock-type-face)
   (cons "[A-Za-z_][-A-Za-z_0-9.]*([^)]+)" font-lock-function-name-face)
   (cons "[^]]\\[[A-Za-z_][-A-Za-z_0-9.]*\\( \\|]\\)" font-lock-variable-name-face))
  "Rules for highlighting LTM topic maps.")

(defvar ltm-mode-map ()
  "Keymap used in LTM mode.")
(when (not ltm-mode-map)
  (setq ltm-mode-map (make-sparse-keymap))
  (define-key ltm-mode-map (read-kbd-macro "C-c C-i") 'ltm-add-int-occurrence)
  (define-key ltm-mode-map (read-kbd-macro "C-c C-o") 'ltm-add-int-occurrence-yank)
  (define-key ltm-mode-map (read-kbd-macro "C-c C-v") 'ltm-add-ext-occurrence-yank))

(defun ltm-add-int-occurrence(topicid occtypeid)
  (interactive "sTopic: \nsOccurrence type: ")
  (insert "{" topicid ", " occtypeid ", [[]]}\n")
  (backward-char 4))
(defun ltm-add-int-occurrence-yank(topicid occtypeid)
  (interactive "sTopic: \nsOccurrence type: ")
  (insert "{" topicid ", " occtypeid ", [[")
  (yank)
  (insert "]]}\n"))
(defun ltm-add-ext-occurrence-yank(topicid occtypeid)
  (interactive "sTopic: \nsOccurrence type: ")
  (insert "{" topicid ", " occtypeid ", \"")
  (yank)
  (insert "\"}\n"))

(defun ltm-mode()
  "Major mode for editing topic maps in LTM format.
\\{ltm-mode-map}"
  (interactive)

  ; Initializing
  (kill-all-local-variables)
  
  ; Setting up font-locking
  (make-local-variable 'font-lock-defaults)
  (setq font-lock-defaults '(ltm-font-lock-keywords nil t nil nil))

  ; Setting up keymap
  (use-local-map ltm-mode-map)
  
  ; Setting up syntax recognition
  (make-local-variable 'comment-start)
  (make-local-variable 'comment-end)
  (make-local-variable 'comment-start-skip)

  (setq comment-start "/* "
	comment-end " */"
	comment-start-skip "/\\*[ \n\t]+")

  ; Setting up syntax table
  (modify-syntax-entry ?* ". 23")
  (modify-syntax-entry ?/ ". 14")
  
  ; Final stuff, then we're done
  (setq mode-name "LTM"
	major-mode 'ltm-mode)
  (run-hooks 'ltm-mode-hook))

(provide 'ltm-mode)

;; ltm-mode ends here