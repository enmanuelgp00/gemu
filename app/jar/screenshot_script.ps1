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
		public static extern bool GetClientRect(IntPtr hWind, out RECT rect);
		
		[DllImport("user32.dll")]
		public static extern bool ClientToScreen(IntPtr hWind, ref POINT point);
				
		[DllImport("user32.dll")]
		public static extern bool GetSystemMetrics(IntPtr nIndex);
		
		[DllImport("user32.dll")]
		public static extern bool GetWindowRect(IntPtr hWind, out RECT rect);
		
		[DllImport("user32.dll")]
		public static extern bool ShowWindow(IntPtr hWind, int nCmdShow);

		[DllImport("user32.dll")]
		public static extern bool IsIconic(IntPtr hWind);
		

	}

	[StructLayout(LayoutKind.Sequential)]
	public struct POINT {
		public int X; public int Y;
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
	[void][Win32]::ShowWindow($hWind, $SW_RESTORE);
}

[void][Win32]::SetForegroundWindow($process.MainWindowHandle)

Start-Sleep -Milliseconds 500


$clientRect = New-Object RECT
[void][Win32]::GetClientRect($process.MainWindowHandle, [ref]$clientRect )

$clientPoint = New-Object POINT
$clientPoint.X = 0
$clientPoint.Y = 0

[void][Win32]::ClientToScreen( $hWind, [ref]$clientPoint )

$width = $clientRect.Right - $clientRect.Left
$height = $clientRect.Bottom - $clientRect.Top

$bitmap = New-Object System.Drawing.Bitmap $width, $height
$graphics = [System.Drawing.Graphics]::fromImage($bitmap)
$graphics.CopyFromScreen($clientPoint.X, $clientPoint.Y, 0, 0, $bitmap.size )

[void]$bitmap.Save("main_screenshot.jpg", [System.Drawing.Imaging.ImageFormat]::jpeg)

$bitmap.dispose()
$graphics.dispose()