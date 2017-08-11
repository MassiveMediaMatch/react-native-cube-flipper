
Pod::Spec.new do |s|
  s.name         = "CubeFlipper"
  s.version      = "1.0.0"
  s.summary      = "CubeFlipper"
  s.description  = <<-DESC
                  CubeFlipper
                   DESC
  s.homepage     = ""
  s.license      = "MIT"
  # s.license      = { :type => "MIT", :file => "FILE_LICENSE" }
  s.author             = { "author" => "laurens55@gmail.com" }
  s.platform     = :ios, "7.0"
  s.source       = { :git => "https://github.com/LauR3y/CubeFlipper.git", :tag => "master" }
  s.source_files  = "CubeFlipper/**/*.{h,m}"
  s.requires_arc = true


  s.dependency "React"
  #s.dependency "others"

end

  