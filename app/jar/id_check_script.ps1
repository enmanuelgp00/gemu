
$process
function Main {

		
	if ( $parentId -eq -1 ) { 
	
		$process = Get-WmiObject Win32_Process | Where-Object { $_.Name -like $processName }
	} else {
	
		$process = Get-WmiObject Win32_Process | Where-Object { $_.ProcessId -like $parentId }
	}   

	Find-MainWindowProcess -WmiProcess $process
	
}

function Find-MainWindowProcess {
	param(
		$WmiProcess
	)
	
	if ($WmiProcess -eq $null ) {
		return $null
	}
	
	if ( $WmiProcess.Count -gt 1 ) {
		#write-host "More than a single process :" $WmiProcess.Count
		$wmip = $null
		foreach($p in $WmiProcess) {
			$wmip = Find-MainWindowProcess -WmiProcess $p
			if ( $wmip -ne $null ) {
				return $wmip;
			}
		}
	} else {
	
	
		$id = $WmiProcess.ProcessId
		$diacnosticProcess = Get-Process -Id $id 
		
		write-host $id $($diacnosticProcess.MainWindowHandle -ne 0)
		
		if ( $diacnosticProcess.MainWindowHandle -eq 0 ) {
			#write-host "Not Window found for id :" $id
			$children = Get-WmiObject Win32_Process | Where-Object { $_.ParentProcessId -like $id }
			if ( $children -ne $null ) {
				return Find-MainWindowProcess -WmiProcess $children			
			}
		} 
	}
	return $null;
}

Main




#[System.Management.ManagementBaseObject]
