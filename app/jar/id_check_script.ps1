$process = Get-WmiObject Win32_Process | Where-Object { $_.Name -like $processName }
#[System.Management.ManagementBaseObject]
function Find-MainWindowProcess {
	param(
		$WmiProcess
	)
	if ( $WmiProcess.Count -gt 1 ) {
		write-host "More than a single process :" + $WmiProcess.Count
		$wmip = -1
		foreach($p in $WmiProcess) {
			$wmip = Find-MainWindowProcess -WmiProcess $p
			if ( $wmip -ne $null ) {
				return $wmip;
			}
		}
	} else {
	
		$id = $WmiProcess.ProcessId
		write-host "current id : " + $id
		$diacnosticProcess = Get-Process -Id $id
		if ( $diacnosticProcess.MainWindowHandle -ne 0 ) {
			return $diacnosticProcess.Id
		} else {
		
			$children = Get-WmiObject Win32_Process | Where-Object { $_.ParentProcessId -like $id }
			if ( $children -ne $null ) {
				return Find-MainWindowProcess -WmiProcess $children			
			}
		}
	}
	return -1;
}

Find-MainWindowProcess -WmiProcess $process
