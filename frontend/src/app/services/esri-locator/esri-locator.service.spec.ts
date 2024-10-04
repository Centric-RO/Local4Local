import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { EsriLocatorService } from './esri-locator.service';
import { EsriSuggestionResult } from '../../models/esri-suggestion-response.model';
import AddressCandidate from '@arcgis/core/rest/support/AddressCandidate';
import { loadModules } from 'esri-loader';

jest.mock('esri-loader', () => ({
    loadModules: jest.fn()
}));

describe('EsriLocatorService', () => {
    let service: EsriLocatorService;
    let mockLocator: any;
    let mockPoint: any;

    beforeEach(() => {
        TestBed.configureTestingModule({});
        service = TestBed.inject(EsriLocatorService);

        mockLocator = {
            suggestLocations: jest.fn(),
            addressToLocations: jest.fn()
        };
        mockPoint = jest.fn();

        (loadModules as jest.Mock).mockReturnValue(of([mockLocator, mockPoint]));
    });

    describe('getSuggestions', () => {
        it('should return an empty array if input is empty', () => {
            service.getSuggestions('').subscribe(suggestions => {
                expect(suggestions).toEqual([]);
            });
        });

        it('should return suggestions on successful response', () => {
            const mockSuggestions: EsriSuggestionResult[] = [
                { text: 'Suggestion 1', magicKey: 'key1' },
                { text: 'Suggestion 2', magicKey: 'key2' }
            ];
            mockLocator.suggestLocations.mockReturnValue(Promise.resolve(mockSuggestions));

            service.getSuggestions('test').subscribe(suggestions => {
                expect(suggestions).toEqual(mockSuggestions);
            });
        });

        it('should handle errors and return an empty array', () => {
            mockLocator.suggestLocations.mockReturnValue(Promise.reject('Error'));

            service.getSuggestions('test').subscribe(suggestions => {
                expect(suggestions).toEqual([]);
            });
        });
    });

    describe('findLocationBasedOnSuggestion', () => {
        it('should return address candidates on successful response', () => {
            const mockCandidates: AddressCandidate[] = [
                {} as AddressCandidate
            ];
            mockLocator.addressToLocations.mockReturnValue(Promise.resolve(mockCandidates));

            const suggestion: EsriSuggestionResult = { text: 'Selected Address', magicKey: 'key' };
            service.findLocationBasedOnSuggestion(suggestion).subscribe(candidates => {
                expect(candidates).toEqual(mockCandidates);
            });
        });

        it('should handle errors and return an empty array', () => {
            mockLocator.addressToLocations.mockReturnValue(Promise.reject('Error'));

            const suggestion: EsriSuggestionResult = { text: 'Selected Address', magicKey: 'key' };
            service.findLocationBasedOnSuggestion(suggestion).subscribe(candidates => {
                expect(candidates).toEqual([]);
            });
        });
    });
});
