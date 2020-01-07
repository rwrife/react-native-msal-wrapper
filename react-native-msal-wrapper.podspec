require 'json'
package = JSON.parse(File.read(File.join(__dir__, 'package.json')))
Pod::Spec.new do |s|
  s.name           = 'react-native-msal-wrapper'
  s.version        = package['version']
  s.summary        = package['description']
  s.description    = package['description']
  s.license        = package['license']
  s.author         = package['author']
  s.source         = { git: 'http://github.com' }
  s.homepage       = 'http://www.microsoft.com'
  s.requires_arc   = true
  s.platform       = :ios, '11.0'
  s.preserve_paths = 'LICENSE', 'README.md', 'package.json', 'index.js'
  s.source_files   = 'ios/*.{h,m}'
  s.dependency 'React'
  s.dependency 'MSAL'
end
