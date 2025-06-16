package gemu.game;

import gemu.system.*;
import gemu.file.*;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Set;     
import java.util.TreeSet;     
import java.util.HashSet;     
import java.util.HashMap;

public class Game {
	private GameInfo info;
	
	public static final Set<String> tagsCollection = new TreeSet<String>();
	
	public static final int COMPRESSION_STATE_FREE = 0;
	public static final int COMPRESSION_STATE_COMPRESSING = 1;
	public static final int COMPRESSION_STATE_DECOMPRESSING = -1;
	
	public Game( Launcher launcher ) {
		this.info = new GameInfo( launcher );
		setLauncher( launcher ).
		setFavorite( false );
		
		addTagsToCollection();
	}
	
	private void addTagsToCollection() {
		for ( String s : getTags() ) {
			Game.tagsCollection.add( s );
		}
	}
	
	public Game( CompactLauncher launcher ) {		
		this.info = new GameInfo( launcher );
		setLauncher( launcher ).
		setFavorite( false ); 
		addTagsToCollection();
	}
	
	public Game( GameInfo info ) {
		this.info = info;
		if ( info.get( GameInfo.Key.name ).length == 0 ) {
			GameNames.interpret( this , new GameNames.InterpreterListener() {
				@Override
				public void onVersionInterpreted( String v ) {
					System.out.println( v );
				}
				@Override
				public void onSitesInterpreted( String[] sites ) {
					System.out.println( sites );
				
				}
				@Override
				public void onNameInterpreted( String name ) {
					System.out.println( name );
				
				}
			});
		}  
		addTagsToCollection();
	}
	
	
	public Game setLauncher( Launcher launcher ) {
		String path = launcher.getAbsolutePath().substring( getFolder().getAbsolutePath().length() );
		info.set( GameInfo.Key.launcher, path );
		info.commit();
		return this;
	}
	
	public Game setLauncher( CompactLauncher launcher ) {
		String path = launcher.getAbsolutePath().substring( getFolder().getAbsolutePath().length() );
		info.set( GameInfo.Key.launcher, path );
		info.commit();		
		return this;
	}
	
	public Launcher getLauncher() {
		return new Launcher ( info.getFolder().getAbsolutePath() + "/" + info.get( GameInfo.Key.launcher )[0] );
	}	
	
	public Game setName( String name ) {
		info.set( GameInfo.Key.name, name );
		info.commit();
		return this;
	}
	
	public String getName() {
		String[] names = info.get( GameInfo.Key.name );
		if ( names.length > 0 ) { 
			return names[0];
		}
		Launcher launcher = getLauncher();
		if ( CompactFile.isCompactFile( launcher ) ) {
			return new CompactFile( launcher ).getParentRootFile().getBaseName();
		}
		
		return getFolder().getName();
	}
	public Game removeTag( String tag ) {
		info.modif( GameInfo.Key.tags ).remove( tag );
		info.commit();
		return this;
	}
	public Game addTag( String tag ) {
		info.add( GameInfo.Key.tags, tag );
		info.commit();
		return this;
	}
	
	public String[] getTags() {
		return info.get( GameInfo.Key.tags );
	}
	
	
	public void play() {
		getLauncher().run();
	}
	public void setFolder( Folder folder ) {
		info.setFolder( folder );
	}
	public Folder getFolder() {
		return info.getFolder();
	}
	
	public void addScreenshot( File file ) {
		if ( file.exists() ) {
			info.modif( GameInfo.Key.screenshots ).add( file.getName() );
			info.commit();		
		}
	}
	
	public File[] getScreenshots() {
		String[] names = info.get( GameInfo.Key.screenshots );
		File[] screenshots = new File[ names.length ];
		
		for ( int i = 0; i < names.length; i++ ) {
			screenshots[i] = new File( getFolder() + "\\" + names[i] );
		}
		
		return screenshots;
	}
	public long length() {
		Launcher launcher = getLauncher();
		if ( CompactFile.isCompactFile( launcher )) {
			return new CompactFile( launcher ).getParentRootFile().length();
		}
		return getFolder().length();
	}
	
	public Game setFavorite( boolean b ) {
		info.set( GameInfo.Key.favorite, String.valueOf( b ) );
		info.commit();
		return this;
	}
	
	public boolean isFavorite() {
		String[] arr = info.get( GameInfo.Key.favorite );
		if ( arr.length > 0 ) {
			return Boolean.parseBoolean( info.get( GameInfo.Key.favorite )[0] );		
		}
		return false;
	}
	
	public Game setVersion( String value ) {
		if ( value.charAt(0) != 'v') {
			value = 'v' + value;
		}
		info.set( GameInfo.Key.version, value );
		info.commit();
		return this;
	}
	
	public String getVersion() {
		String[] list = info.get( GameInfo.Key.version );
		if ( list.length > 0 ) {
			String value = list[0];
			if ( value.charAt(0) != 'v') {
				value = 'v' + value;
			};
			setVersion( value );
			return value;
		}
		return null;
	}
	
	public boolean isCompressed() {
		return CompactFile.isCompactFile( getLauncher() );
	}
	
	public void openFolder() {
		Shell.exec( new Shell.Command("explorer", getFolder().getAbsolutePath() ) );
	}
	
	public int getCompressionState() {
		for ( Compressions.CProcess p : Compressions.getList() ) {
			File f = p.getFile();
			File tmp = null;
			if ( f.matchesPath( getFolder() ) ) {
				return COMPRESSION_STATE_COMPRESSING;
			} else if ( ( tmp = new CompactFile( getLauncher() ).getParentRootFile() ) != null ) {
				if ( tmp.matchesPath( f ) ) {
					return COMPRESSION_STATE_DECOMPRESSING;				
				}
			}
		}
		
		return COMPRESSION_STATE_FREE;
		
	}
	
	
	public void compress( OnSuccessListener listener ) {
		if ( getCompressionState() == COMPRESSION_STATE_FREE ) {
		
			if ( !isCompressed() ) {  			
				File folder = getFolder();
				Launcher launcher = getLauncher();
				
				Compressions.add( new Compressions.CompressProcess( folder, new Compressions.OnCompressListener() {
					@Override
					public void onStart() {
						System.out.println("\n[ Compressing : " + getName() + " ]"); 
						listener.onStart();
					}
					@Override
					public void onError() {
						listener.onError(); 
					}
					@Override
					public void onSuccess( CompactFile compactFile ) {
					
						for ( CompactFile f : compactFile.listFiles() ) {
							if (f.hasSameName( launcher )) {           
								Log.info( f + " : Relocating launcher");
								setLauncher( new CompactLauncher( f ));
								break;
							}
						}
						listener.onSuccess();
					}
					
				}, "screenshot.jpg", "*." + GameInfo.EXTENSION ) );
			
				
			} else {
				System.out.println( "[ " + getName() + " ] : is already compressed");
			}  		
		}      		
	}
	
	public void decompress( OnSuccessListener listener ) {
		if ( getCompressionState() == COMPRESSION_STATE_FREE ) {
		
			if ( isCompressed() ) { 
			
				CompactFile compression = new CompactFile( getLauncher() );
				Launcher launcher = getLauncher();
				
				Compressions.add( new Compressions.DecompressProcess( compression , new Compressions.OnDecompressListener() {
					@Override
					public void onStart() {					
						Log.info("\n[ Decompressing : " + getName() + " ]" );
						listener.onStart();
					}
					@Override
					public void onSingleCompactFileFoundIn( Folder folder ) {
						/*
						File more = Games.defineMoreFileIn( folder );
						if ( !more.exists() ) {
							try {
								more.createNewFile();
								Log.info("More than a single compact file found : more sign created");
							
							} catch ( Exception e) {
								Log.error( e.getMessage() );
								e.printStackTrace();
							}
						}
						*/
						
					}                                                    
					@Override
					public void onNonSingleCompactFileFoundIn( Folder folder ) {
						/*
						File more = Games.defineMoreFileIn( folder );
						if ( more.exists() ) {
							more.delete();                                                            
							Log.info("Single compact file found : more sign delete");
						}
						*/
					}
					@Override
					public void onError() {
						listener.onError();
					}
					@Override
					public void onSuccess( Folder folder ) {
						if ( !getFolder().matchesPath( folder ) ) {
							setFolder( folder );  		
						}
						
						for ( File f : folder.listFiles() ) {
							if ( f.hasSameName( launcher ) ) {
								setLauncher( new Launcher( f ) );
								break;
							}
						}
						/*
						String oldPath = compression.getAbsolutePath();
						String root = compression.getParentRootFile().getAbsolutePath();
						String newPath = oldPath.substring( root.length() );
						
						if ( !getFolder().matchesPath( folder ) ) {
							Log.info(getFolder() + " : Relocating folder");
							setFolder( folder );               
						}
						
						setLauncher( new Launcher( getFolder().getAbsolutePath() + "/" + newPath ) );
						*/
						listener.onSuccess();
					}
				}));
			
			} else {
				System.out.println( "[ " + getName() + " ] : is not compressed");
			}
		}
		
	}
	
	
	
}