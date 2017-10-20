#NoTrayIcon

; As no resource names are specified, they will be set automatically to 201+
#Region ;**** Directives created by AutoIt3Wrapper_GUI ****
#AutoIt3Wrapper_Res_Icon_Add=green.ico
#AutoIt3Wrapper_Res_Icon_Add=yellow.ico
#AutoIt3Wrapper_Res_Icon_Add=red.ico
#EndRegion ;**** Directives created by AutoIt3Wrapper_GUI ****

#include <GUIConstantsEx.au3>
#include <MsgBoxConstants.au3>
#include <TrayConstants.au3>
#include <WindowsConstants.au3>
#include <XML.au3>
#include <Misc.au3>

Opt("TrayIconHide", 0)
Opt("TrayMenuMode", 7)
Opt("TrayOnEventMode", 1)
;AutoItSetOption("TrayIconDebug", 1)
_Singleton("Ampel")

Global Const $STATE_BLUE = 1
Global Const $STATE_YELLOW = 2
Global Const $STATE_RED = 3

Global $url = "URL" ; Add your Url to your Jenkins here!
Global $lastState = $STATE_BLUE
Global $lastMessage = ""

Global $counter = 60

TrayCreateItem("Details")
TrayItemSetOnEvent(-1, "showDetails")
;TrayCreateItem("Einstellungen")
;TrayItemSetOnEvent(-1, "showSettings")
TrayCreateItem("")
TrayCreateItem("Beenden")
TrayItemSetOnEvent(-1, "ExitScript")

TraySetOnEvent($TRAY_EVENT_PRIMARYDOUBLE, "showDetails")

TraySetState($TRAY_ICONSTATE_SHOW)

While true
   $counter = $counter + 1
   If $counter > 60 Then
      $counter = 0
      updateState()
   EndIf
   Sleep(1000)
Wend

Func ExitScript()
    Exit
EndFunc

Func updateState()
   Local $state = $STATE_BLUE
   Local $message = ""
   Local $icon = $TIP_ICONNONE
   Local $oXML = ObjCreate("Microsoft.XMLDOM")
   $oXML.load(InetRead($url))
   Local $jobs = $oXML.SelectNodes("//dashboard/job")
   For $job In $jobs
      Local $colortext = getJobColor($job)
      If StringLeft($colortext, 6) = "yellow" And $state <> $STATE_RED Then
         $message = appendMessage($message, getJobName($job))
         $state = $STATE_YELLOW
         $icon = $TIP_ICONEXCLAMATION
      EndIf
      If StringLeft($colortext, 3) = "red" Then
        $message = appendMessage($message, getJobName($job))
        $state = $STATE_RED
        $icon = $TIP_ICONHAND
      EndIf
   Next
   If $state = $STATE_BLUE Then
      $message = "Keine Probleme, alles grün."
   EndIf
   If $state <> $lastState Or $lastMessage <> $message Then
;      If @Compiled Then
;         $icon = @ScriptFullPath
;         $icon_id = -$state;
;      Else
         Switch $state
            Case $STATE_BLUE
               $icon = "green.ico"
            Case $STATE_YELLOW
               $icon = "yellow.ico"
            Case Else
               $icon = "red.ico"
         EndSwitch
         $icon_id = -1;
;      EndIf
      TraySetIcon($icon, $icon_id)
      TrayTip("Jenkins Ampel", $message, 0, $icon + $TIP_NOSOUND)
      Sleep(5000)
      TrayTip("clears any tray tip", "", $TIP_ICONNONE + $TIP_NOSOUND)
      TraySetToolTip($message)
      $lastState = $state
      $lastMessage = $message
   EndIf
EndFunc

Func getJobColor($job)
   Return StringLower(StringReplace(String(getXMLText($job, "./color")), "_anime", ""))
EndFunc

Func getJobName($job)
   Return getXMLText($job, "./name")
EndFunc

Func getXMLText($xmlElement, $nodeName)
   Local $node = $xmlElement.SelectSingleNode($nodeName);
   If $node Then
      Return $node.text
   EndIf
   Return ""
EndFunc

Func appendMessage($message, $text)
   If $message <> ""  Then
      Return $message & @CRLF & $text
   EndIf
   Return $text
EndFunc

Func showSettings()
   Local $iOldOpt = Opt("GUICoordMode", 2)
   Local $hGUI = GUICreate("Einstellungen")
   GUISetState()
   GUISetState(@SW_LOCK)
   GUICtrlCreateLabel("URL:", 10, 30)
   Local $idURL = GUICtrlCreateEdit ($url, 0, -1, 300)
   Local $idOK = GUICtrlCreateButton("OK", -1, -1, 85, 25)
   Local $idCancel = GUICtrlCreateButton("Abbrechen", 0, -1, 85, 25)
   GUISetState(@SW_SHOW, $hGUI)
   While 1
      Switch GUIGetMsg()
         Case $GUI_EVENT_CLOSE, $idCancel
            ExitLoop
         Case $idOK
            $url = GUICtrlRead($idURL)
            ExitLoop
      EndSwitch
   WEnd
   GUIDelete($hGUI)
   Opt("GUICoordMode", $iOldOpt)
EndFunc

Func showDetails()
   Local $iOldOpt = Opt("GUICoordMode", 2)
   Local $hGUI = GUICreate("Details")
   Local $oXML = ObjCreate("Microsoft.XMLDOM")
   $oXML.load(InetRead($url))
   Local $jobs = $oXML.SelectNodes("//dashboard/job")
   Local $idListview = GUICtrlCreateListView("Job  |Status|Report  ", 10, 10, 500, 200)
   For $job In $jobs
      Local $value = parseJob($job)
      _MyDebug($value)
      If $value <> "" Then
         GUICtrlCreateListViewItem($value, $idListview)
      EndIf
   Next
   Local $idOK = GUICtrlCreateButton("OK", 10, 520, 85, 25)
   GUISetState(@SW_SHOW, $hGUI)
   While 1
      Switch GUIGetMsg()
         Case $GUI_EVENT_CLOSE
            ExitLoop
         Case $idOK
            ExitLoop
      EndSwitch
   WEnd
   GUIDelete($hGUI)
   Opt("GUICoordMode", $iOldOpt)
EndFunc

Func parseJob($job)
   Local $url = getXMLText($job, "./url") & "api/xml"
   Local $oXML = ObjCreate("Microsoft.XMLDOM")
   $oXML.load(InetRead($url))
   For $healthReport In $oXML.SelectNodes("//freeStyleProject/healthReport")
      Return getJobName($job) & "|" & getJobColor($job) & "|" & getXMLText($healthReport, "./description")
   Next
   Return getJobName($job) & "|" & getJobColor($job) & "|"
EndFunc

Func _MyDebug($sMessage, $iError = @error, $iExtended = @extended)
   If $iError Or $iExtended Then
      $sMessage &= '[ @error = ' & $iError & ' @extended = ' & $iExtended & ' ]'
   EndIf
   DllCall("kernel32.dll", "none", "OutputDebugString", "str", $sMessage)
   Return SetError($iError, $iExtended, '')
EndFunc 