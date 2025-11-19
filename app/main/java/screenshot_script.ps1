add-type -assemblyname system.windows.forms
add-type -assemblyname system.drawing

add-type @"
	using System;
	using System.Runtime.InteropServices;
	public class Win32 {
		
		[DllImport("user32.dll")]
		public static extern bool GetForegroundWindow();

		[DllImport("user32.dll")]
		public static extern bool SetForegroundWindow(IntPtr hWind);

		[DllImport("user32.dll")]
		public static extern bool GetWindowRect(IntPtr hWind, out RECT rect);

		[DllImport("user32.dll")]
		public static extern bool ShowWindow(IntPtr hWind, int nCmdShow);

		[DllImport("user32.dll")]
		public static extern bool IsIconic(IntPtr hWind);
		

	}

	[StructLayout(LayoutKind.Sequential)]
	public struct RECT {
		public int Left; public int Top; public int Right; public int Bottom;
	}
"@

$SW_RESTORE = 9
$SW_HIDE = 0
$SW_SHOW = 5

$process = get-process -id $id
if ( -not $process) { return $false }

$hWind = $process.MainWindowHandle
if ( [Win32]::IsIconic($hWind) ) {
	[Win32]::ShowWindow($hWind, $SW_RESTORE);
}

[Win32]::SetForegroundWindow($process.MainWindowHandle)

Start-Sleep -Milliseconds 500


$rect = New-Object RECT
[Win32]::GetWindowRect($process.MainWindowHandle, [ref]$rect )
$width = $rect.Right - $rect.Left
$height = $rect.Bottom - $rect.Top

$bitmap = New-Object System.Drawing.Bitmap $width, $height
$graphics = [System.Drawing.Graphics]::fromImage($bitmap)
$graphics.CopyFromScreen($rect.Left, $rect.Top, 0, 0, $bitmap.size )
write-host "params: "
write-host $id
write-host $location 
write-host $filename

$bitmap.Save("screenshot.png", [System.Drawing.Imaging.ImageFormat]::png);

